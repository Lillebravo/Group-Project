import { getRequest } from "../api.js";

const autocompleteItems = document.querySelector("#exercise-autocomplete-items");
const exerciseInput = document.querySelector("#exercise-input");

let currentName = "";

exerciseInput.addEventListener("input", async (e) => {
	const input = e.target;
	if (input.value.length === 0) {
		currentName = "";
		exerciseInput.dataset.exerciseId = "";
		autocompleteItems.innerHTML = "";
		autocompleteItems.classList.add("hidden");
		return;
	}

	if (input.value !== currentName) {
		currentName = input.value;
		exerciseInput.dataset.exerciseId = "";
	}

	const exercises = await getRequest(`/exercises/search?keyword=${input.value}`);

	autocompleteItems.innerHTML = "";

	for (const exercise of exercises) {
		const exerciseItem = document.createElement("div");
		const exerciseItemText = document.createElement("span");
		const exerciseItemCategory = document.createElement("span");

		exerciseItemText.textContent = exercise.name;
		exerciseItemCategory.textContent = exercise.category;
		exerciseItemCategory.classList.add("badge");

		exerciseItem.appendChild(exerciseItemText);
		exerciseItem.appendChild(exerciseItemCategory);

		exerciseItem.addEventListener("click", () => {
			input.value = exercise.name;
			currentName = exercise.name;

			input.dataset.exerciseId = exercise.exerciseId;
			input.dataset.category = exercise.category;

			autocompleteItems.innerHTML = "";
			autocompleteItems.classList.add("hidden");

			console.log(`Chose ${exercise.name} ${exercise.exerciseId}`);
		});

		autocompleteItems.appendChild(exerciseItem);
	}

	if (exercises.length === 0) {
		document.querySelector(".autocomplete-items").classList.add("hidden");
	} else {
		document.querySelector(".autocomplete-items").classList.remove("hidden");
	}
});
