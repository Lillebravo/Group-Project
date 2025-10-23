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

  //Hämta övningar från API
  async function fetchExercises() {
    try {
      const res = await fetch("http://localhost:8080/api/exercises");
      if (!res.ok) throw new Error("Fel vid hämtning av övningar");
      const data = await res.json();
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
      const group = ex.muscleGroup || ex.muscle_group || "Övrigt";
      if (!grouped[group]) grouped[group] = [];
      grouped[group].push(ex);
    });

    listContainer.innerHTML = "";

    Object.entries(grouped)
      .sort(([a], [b]) => a.localeCompare(b))
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

// === Filtrering vid sökning ===
searchInput.addEventListener("input", (e) => {
  const keyword = e.target.value.toLowerCase();

  const filtered = allExercises.filter((ex) => {
    const nameMatch = ex.name.toLowerCase().includes(keyword);
    const muscleMatch = (ex.muscleGroup || ex.muscle_group || "")
      .toLowerCase()
      .includes(keyword);
    return nameMatch || muscleMatch;
  });

  renderExercises(filtered);
});


  //Popup-funktion
  function openModal(ex) {
    modal.classList.remove("hidden");
    modalName.textContent = ex.name;
    modalDescription.textContent = ex.description || "Ingen beskrivning tillgänglig.";
    modalMuscle.textContent = ex.muscleGroup || ex.muscle_group || "-";
    modalEquipment.textContent = ex.equipment || "-";
    modalSource.textContent = ex.source || "-";

    addToPlan.onclick = () => {
      alert(`Övningen "${ex.name}" har lagts till i ditt schema (simulerat).`);
      modal.classList.add("hidden");
    };
  }

  //Stäng modal
  closeModal.addEventListener("click", () => modal.classList.add("hidden"));

  //Initiera sidan
  fetchExercises();
});
