import { getRequest, postRequest, putRequest } from "../api.js";

document.addEventListener("DOMContentLoaded", async () => {
	const listContainer = document.getElementById("exerciseList");
	const searchInput = document.getElementById("searchInput");
	const modal = document.getElementById("exerciseModal");
	const closeModal = document.getElementById("closeModal");
	const addToPlan = document.getElementById("addToPlan");

	const modalName = document.getElementById("modalName");
	const modalDescription = document.getElementById("modalDescription");
	const modalMuscle = document.getElementById("modalMuscle");
	const modalEquipment = document.getElementById("modalEquipment");
	const modalSource = document.getElementById("modalSource");

	let allExercises = [];

	//Hjälpfunktion för token
	function getJwtToken() {
		const stored = localStorage.getItem("token");
		if (!stored) return null;

		try {
			const { type, token } = JSON.parse(stored);
			return `${type} ${token}`;
		} catch (err) {
			console.error("Kunde inte läsa token:", err);
			return null;
		}
	}

	//Hämta övningar från API
	async function fetchExercises() {
		try {
			const data = await getRequest("/exercises"); // använder api.js med token

			if (!data || data.error) {
				throw new Error(data?.error || "Fel vid hämtning av övningar");
			}

			allExercises = data;
			renderExercises(data);
		} catch (err) {
			console.error(err);
			listContainer.innerHTML = `<p class="loading-text">Kunde inte ladda övningar.</p>`;
		}
	}

	//Rendera övningar grupperat per muskelgrupp
	function renderExercises(exercises) {
		const grouped = {};

		exercises.forEach((ex) => {
			const group = ex.isCustomExercise
				? "Egna Övningar"
				: ex.category || ex.muscleGroup || ex.muscle_group || "Övrigt";
			if (!grouped[group]) grouped[group] = [];
			grouped[group].push(ex);
		});

		listContainer.innerHTML = "";

		Object.entries(grouped)
			.sort(([a], [b]) => {
				if (a === "Egna Övningar") return -1;
				if (b === "Egna Övningar") return 1;
				return a.localeCompare(b);
			})
			.forEach(([groupName, groupExercises]) => {
				const groupEl = document.createElement("div");
				groupEl.className = "exercise-group";

				const header = document.createElement("div");
				header.className = "group-header";
				header.textContent = groupName;

				const expandIcon = document.createElement("span");
				expandIcon.textContent = "▼";
				header.appendChild(expandIcon);

				const list = document.createElement("div");
				list.className = "group-exercises";

				groupExercises
					.sort((a, b) => a.name.localeCompare(b.name))
					.forEach((ex) => {
						const item = document.createElement("div");
						item.className = "exercise-item";
						item.textContent = ex.name;
						item.addEventListener("click", () => openModal(ex));
						//Redigera knapp
						if (ex.isCustomExercise) {
							const editBtn = document.createElement("button");
							editBtn.textContent = "✏️";
							editBtn.className = "edit-btn";
							editBtn.addEventListener("click", (event) => {
								event.stopPropagation();
								openEditModal(ex);
							});
							item.appendChild(editBtn);
						}
						//Ta bort knapp
						const deleteBtn = document.createElement("button");
						deleteBtn.textContent = "🗑️";
						deleteBtn.className = "delete-btn";
						deleteBtn.addEventListener("click", (event) => {
							event.stopPropagation();
							deleteCustomExercise(ex);
						});
						item.appendChild(deleteBtn);

						list.appendChild(item);
					});

				header.addEventListener("click", () => {
					list.classList.toggle("active");
					expandIcon.textContent = list.classList.contains("active") ? "▲" : "▼";
				});

				groupEl.appendChild(header);
				groupEl.appendChild(list);
				listContainer.appendChild(groupEl);
			});
	}

	//Filtrering vid sökning (namn + muskelgrupp)
	searchInput.addEventListener("input", (e) => {
		const keyword = e.target.value.toLowerCase();
		const filtered = allExercises.filter((ex) => {
			const nameMatch = (ex.name || "").toLowerCase().includes(keyword);
			const categoryMatch = (ex.category || ex.muscleGroup || ex.muscle_group || "")
				.toLowerCase()
				.includes(keyword);

			return nameMatch || categoryMatch;
		});
		renderExercises(filtered);
	});

	//Popup-funktion
	function openModal(ex) {
		modal.classList.remove("hidden");
		modalName.textContent = ex.name;
		modalDescription.textContent = ex.description || "Ingen beskrivning tillgänglig.";
		modalMuscle.textContent = ex.category || "-";
		modalEquipment.textContent = ex.equipment || "-";
		modalSource.textContent = ex.source || "-";

		addToPlan.onclick = () => {
			alert(`Övningen "${ex.name}" har lagts till i ditt schema (simulerat).`);
			modal.classList.add("hidden");
		};
	}

	//Stäng modal
	closeModal.addEventListener("click", () => modal.classList.add("hidden"));

	//Modalhantering för egna övningar
	const createBtn = document.getElementById("createExerciseBtn");
	const createModal = document.getElementById("createExerciseModal");
	const closeCreateModal = document.getElementById("closeCreateModal");
	const saveCustomBtn = document.getElementById("saveCustomExercise");

	createBtn.addEventListener("click", () => {
		createModal.classList.remove("hidden");
	});

	closeCreateModal.addEventListener("click", () => {
		createModal.classList.add("hidden");
	});

	//Spara egen övning
	saveCustomBtn.addEventListener("click", async () => {
		const name = document.getElementById("customName").value.trim();
		const category = document.getElementById("customCategory").value.trim();
		const description = document.getElementById("customDescription").value.trim();

		if (!name || !category) {
			alert("Namn och muskelgrupp krävs.");
			return;
		}

		const token = getJwtToken();
		if (!token) {
			alert("Du måste vara inloggad för att skapa egna övningar.");
			return;
		}

		try {
			const data = await postRequest("/exercises/custom", { name, category, description });

			if (!data || data.error) {
				throw new Error(data?.error || "Kunde inte spara övning");
			}

			alert("Övning skapad!");
			createModal.classList.add("hidden");

			document.getElementById("customName").value = "";
			document.getElementById("customCategory").value = "";
			document.getElementById("customDescription").value = "";

			fetchExercises();
		} catch (err) {
			console.error("Fel vid sparande:", err);
			alert("Något gick fel vid sparande av övning.");
		}
	});

	//Modal för redigering av egna övningar
	const editModal = document.getElementById("editExerciseModal");
	const closeEditModal = document.getElementById("closeEditModal");
	const saveEditedExercise = document.getElementById("saveEditedExercise");

	let editingExerciseId = null;

	function openEditModal(exercise) {
		editingExerciseId = exercise.exerciseId;

		document.getElementById("editName").value = exercise.name || "";
		document.getElementById("editCategory").value = exercise.category || "";
		document.getElementById("editDescription").value = exercise.description || "";

		editModal.classList.remove("hidden");
	}

	closeEditModal.addEventListener("click", () => {
		editModal.classList.add("hidden");
		editingExerciseId = null;
	});

	//Klick på "Spara ändringar"
	saveEditedExercise.addEventListener("click", async () => {
		const name = document.getElementById("editName").value.trim();
		const category = document.getElementById("editCategory").value.trim();
		const description = document.getElementById("editDescription").value.trim();

		if (!name || !category) {
			alert("Namn och muskelgrupp krävs.");
			return;
		}

		await updateCustomExercise(editingExerciseId, name, category, description);

		editModal.classList.add("hidden");
		editingExerciseId = null;
	});

	//Funktion för PUT-anrop (uppdatera övning)
	async function updateCustomExercise(id, name, category, description) {
		try {
			const response = await putRequest(`/exercises/custom/${id}`, {
				name,
				category,
				description,
			});

			if (!response || response.error) {
				throw new Error(response?.error || "Kunde inte uppdatera övning");
			}

			alert("Övning uppdaterad!");
			fetchExercises();
		} catch (err) {
			console.error("Fel vid uppdatering:", err);
			alert("Något gick fel vid uppdatering av övning.");
		}
	}

	//Funktion för DELETE-anrop (ta bort övning)
	async function deleteCustomExercise(exercise) {
		const confirmDelete = confirm(
			`Är du säker på att du vill ta bort övningen "${exercise.name}"?`
		);
		if (!confirmDelete) return;

		try {
			const response = await fetch(
				`http://localhost:8080/api/exercises/custom/${exercise.exerciseId}`,
				{
					method: "DELETE",
					headers: {
						"Content-Type": "application/json",
						Authorization: getJwtToken(),
					},
				}
			);

			const data = await response.json().catch(() => ({}));

			if (!response.ok) {
				throw new Error(data?.error || "Kunde inte ta bort övning");
			}

			alert("Övningen har tagits bort!");
			fetchExercises();
		} catch (err) {
			console.error("Fel vid borttagning:", err);
			alert("Något gick fel vid borttagning av övning.");
		}
	}

	//Initiera sidan
	fetchExercises();
});
