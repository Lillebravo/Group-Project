import { getJwtTokenInfo, postRequest } from "../../api.js";
import { emptyImg, isElementBefore, secondsToMinutesAndSeconds } from "../../utils.js";

const html = String.raw;

const exercisesList = document.querySelector(".workout-exercises");

const addExerciseModal = document.querySelector("#add-exercise-modal");
const exerciseTemplate = document.querySelector("#workout-exercise-template");
const exerciseSetTemplate = document.querySelector("#exercise-set-template");
const exerciseSetsDropdownTemplate = document.querySelector("#exercise-sets-dropdown-template");
const exerciseSetsDropdownItemTemplate = document.querySelector(
	"#exercise-sets-dropdown-item-template"
);

const exercises = [];

let dragging = null;
let currentlyEditingExercise = false;

function makeExercisesList() {
	const addExerciseBtn = document.createElement("button");
	addExerciseBtn.textContent = "Lägg till övning";
	addExerciseBtn.classList.add("add-workout");
	addExerciseBtn.addEventListener("click", (e) => {
		e.preventDefault();

		addExerciseModal.querySelector("#exercise-modal-description").textContent =
			"Lägg till övning i nytt schema";
		addExerciseModal.classList.remove("hidden");
	});
	exercisesList.appendChild(addExerciseBtn);
}

addExerciseModal.querySelector("#submit-exercise-modal").addEventListener("click", async (e) => {
	e.preventDefault();

	const exerciseInput = addExerciseModal.querySelector("#exercise-input");
	const exerciseSets = addExerciseModal.querySelector("#exercise-sets");
	const exerciseReps = addExerciseModal.querySelector("#exercise-reps");
	const exerciseWeight = addExerciseModal.querySelector("#exercise-weight");
	const exerciseRestTime = addExerciseModal.querySelector("#exercise-rest-time");

	const restTime = exerciseRestTime.valueAsNumber;

	if (
		!exerciseInput.value ||
		!exerciseInput.dataset.exerciseId ||
		!exerciseSets.value ||
		!exerciseReps.value ||
		!exerciseWeight.value
	) {
		alert("Fyll i alla fält");
		return;
	}

	const sets = Array.from({ length: exerciseSets.valueAsNumber }).map((_, i) => ({
		setNumber: i + 1,
		targetReps: exerciseReps.valueAsNumber,
		targetWeight: exerciseWeight.valueAsNumber,
	}));

	const newExercise = {
		exerciseId: +exerciseInput.dataset.exerciseId,
		exerciseName: exerciseInput.value,
		category: exerciseInput.dataset.category,
		restTime: isNaN(restTime) ? 0 : restTime,
		sets,
		orderIndex: exercises.length,
	};

	exercises.push(newExercise);

	const exerciseElement = makeExercise(newExercise);

	exercisesList.insertBefore(exerciseElement, exercisesList.lastChild);

	addExerciseModal.classList.add("hidden");
	addExerciseModal.querySelector("#exercise-input").value = "";
	addExerciseModal.querySelector("#exercise-sets").value = "";
	addExerciseModal.querySelector("#exercise-reps").value = "";
	addExerciseModal.querySelector("#exercise-weight").value = "";
	addExerciseModal.querySelector("#exercise-rest-time").value = "";
});

function makeExercise(exercise) {
	const exerciseElement = exerciseTemplate.content.cloneNode(true).querySelector(".exercise");

	exerciseElement.dataset.exerciseId = exercise.exerciseId;

	exerciseElement.addEventListener("dragstart", dragStart);
	exerciseElement.addEventListener("dragenter", dragEnter);
	exerciseElement.addEventListener("dragover", dragOver);
	exerciseElement.addEventListener("dragend", dragEnd);

	exerciseElement.querySelector(".exercise-name").textContent = exercise.exerciseName;
	exerciseElement.querySelector(".exercise-category").textContent = exercise.category;
	exerciseElement.querySelector(".exercise-index").textContent = exercise.orderIndex + 1;

	const restTimeValue = exerciseElement.querySelector(".exercise-rest-time-value");
	restTimeValue.textContent = secondsToMinutesAndSeconds(exercise.restTime);

	exerciseElement.querySelector(".exercise-rest-time-edit").addEventListener("click", () => {
		editRestTime(exercise, exerciseElement);
	});

	const exerciseSets = exerciseElement.querySelector(".exercise-sets");
	for (const set of exercise.sets) {
		const exerciseSetElement = makeExerciseSet(set);
		exerciseSets.appendChild(exerciseSetElement);
	}

	if (exercise.sets.length > 0) {
		const dropdownButton = document.createElement("button");
		dropdownButton.classList.add("exercise-sets-dropdown-btn");

		dropdownButton.insertAdjacentHTML(
			"afterbegin",
			`<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-chevron-down-icon lucide-chevron-down"><path d="m6 9 6 6 6-6"/></svg>`
		);

		exerciseSets.appendChild(dropdownButton);
	}

	const exerciseSetsDropdown = makeExerciseSetsDropdown(exercise.sets, exerciseSets);
	exerciseSets.appendChild(exerciseSetsDropdown);

	exerciseElement.querySelector(".delete-exercise").addEventListener("click", () => {
		e.preventDefault();
		deleteExercise(exercise, exerciseElement);
	});

	return exerciseElement;
}

function makeExerciseSet(set) {
	const exerciseSetElement = exerciseSetTemplate.content
		.cloneNode(true)
		.querySelector(".exercise-set");

	exerciseSetElement.dataset.setNumber = set.setNumber;

	exerciseSetElement.querySelector(".exercise-set-reps").textContent = set.targetReps;
	exerciseSetElement.querySelector(".exercise-set-weight").textContent = `${set.targetWeight} kg`;

	return exerciseSetElement;
}

function makeExerciseSetsDropdown(sets, exerciseSets) {
	const exerciseSetsDropdown = exerciseSetsDropdownTemplate.content
		.cloneNode(true)
		.querySelector(".exercise-sets-dropdown");

	const dropdownItems = exerciseSetsDropdown.querySelector(".exercise-sets-dropdown-items");

	for (const set of sets) {
		const item = exerciseSetsDropdownItemTemplate.content
			.cloneNode(true)
			.querySelector(".exercise-sets-dropdown-item");

		item.querySelector(".exercise-set-name").textContent = `Set ${set.setNumber}`;
		item.querySelector(
			".exercise-sets-dropdown-item-reps .exercise-sets-dropdown-item-value"
		).value = set.targetReps;
		item.querySelector(
			".exercise-sets-dropdown-item-weight .exercise-sets-dropdown-item-value"
		).value = set.targetWeight;

		dropdownItems.appendChild(item);
	}

	exerciseSetsDropdown
		.querySelector(".exercise-sets-dropdown-save-btn")
		.addEventListener("click", async (e) => {
			e.preventDefault();

			const dropdownNodes = exerciseSetsDropdown.querySelectorAll(".exercise-sets-dropdown-item");

			for (const [index, item] of dropdownNodes.entries()) {
				const reps = item.querySelector(
					".exercise-sets-dropdown-item-reps .exercise-sets-dropdown-item-value"
				);
				const weight = item.querySelector(
					".exercise-sets-dropdown-item-weight .exercise-sets-dropdown-item-value"
				);

				sets[index] = {
					targetReps: reps.valueAsNumber,
					targetWeight: weight.valueAsNumber,
				};

				exerciseSetsDropdown.classList.toggle("hidden");

				const exerciseSetItem = exerciseSets.querySelector(
					`.exercise-set[data-set-number="${index + 1}"]`
				);

				exerciseSetItem.querySelector(".exercise-set-reps").textContent = reps.valueAsNumber;
				exerciseSetItem.querySelector(
					".exercise-set-weight"
				).textContent = `${weight.valueAsNumber} kg`;
			}
		});

	exerciseSets.querySelector(".exercise-sets-dropdown-btn").addEventListener("click", (e) => {
		e.preventDefault();
		exerciseSetsDropdown.classList.toggle("hidden");
	});

	return exerciseSetsDropdown;
}

addExerciseModal.querySelector("#close-exercise-modal").addEventListener("click", (e) => {
	e.preventDefault();
	addExerciseModal.classList.add("hidden");
});

async function editRestTime(exercise, exerciseElement) {
	const element = exerciseElement.querySelector(".exercise-rest-time-value");
	const button = exerciseElement.querySelector(".exercise-rest-time-edit");

	const oldElement = exerciseElement.querySelector(".exercise-rest-time-value").cloneNode(true);
	const oldButton = exerciseElement.querySelector(".exercise-rest-time-edit").cloneNode(true);
	const oldValue = exercise.restTime;

	const newElement = document.createElement("input");
	newElement.type = "number";
	newElement.value = exercise.restTime;
	newElement.classList.add("input", "exercise-rest-time-value");

	const newButton = document.createElement("button");
	newButton.classList.add("exercise-rest-time-edit", "confirm");
	newButton.insertAdjacentHTML(
		"afterbegin",
		`<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-check-icon lucide-check"><path d="M20 6 9 17l-5-5"/></svg>`
	);

	element.replaceWith(newElement);
	button.replaceWith(newButton);

	const revert = (restTime) => {
		newElement.replaceWith(oldElement);
		newButton.replaceWith(oldButton);

		oldElement.textContent = secondsToMinutesAndSeconds(restTime);

		oldButton.addEventListener("click", () => {
			editRestTime(exercise, exerciseElement);
		});
	};

	newButton.addEventListener("click", async () => {
		const restTime = newElement.valueAsNumber;

		if (isNaN(restTime)) {
			alert("Fyll i vilotid");
			return;
		}

		if (restTime < 0) {
			alert("Vilotid kan inte vara negativ");
			return;
		}

		if (restTime == oldValue) {
			revert(oldValue);
			return;
		}

		exercise.restTime = restTime;

		revert(restTime);
	});
}

async function deleteExercise(exercise, exerciseElement) {
	const confirmation = confirm(
		`Är du säker på att du vill ta bort ${exercise.exerciseName} från schemat?`
	);
	if (!confirmation) return;

	exercises.splice(
		exercises.findIndex((exerciseItem) => exerciseItem.exerciseId == exercise.exerciseId),
		1
	);

	exerciseElement.remove();

	refreshExercisesIndex();
}

document.querySelector("#submit-workout-form").addEventListener("click", async (e) => {
	e.preventDefault();

	const workoutName = document.querySelector("#workout-name").value;

	if (workoutName.length === 0) {
		alert("Fyll i namnet på schemat");
		return;
	}

	const workoutResponse = await postRequest("/workouts", { name: workoutName });

	if (workoutResponse.error) {
		alert(workoutResponse.error);
		return;
	}

	const exercisesToAdd = exercises
		.sort((a, b) => a.orderIndex - b.orderIndex)
		.map((exercise) => ({
			exerciseId: exercise.exerciseId,
			sets: exercise.sets,
			restTime: exercise.restTime,
		}));

	for (const exercise of exercisesToAdd) {
		await postRequest(`/workouts/${workoutResponse.workoutId}/exercises`, exercise);
	}

	window.location.href = "/workouts";
});

function dragStart(e) {
	if (currentlyEditingExercise) {
		e.preventDefault();
		return;
	}

	e.dataTransfer.setDragImage(emptyImg, 0, 0);
	e.dataTransfer.effectAllowed = "move";
	e.target.classList.add("dragging");

	dragging = e.target;
}

function dragEnter(e) {
	// Stop not-allowed cursor flickering
	e.preventDefault();
}

function dragOver(e) {
	// Stop not-allowed cursor
	e.preventDefault();

	if (dragging.parentNode !== e.target.parentNode) return;

	if (isElementBefore(dragging, e.target)) {
		e.target.parentNode.insertBefore(dragging, e.target);
	} else {
		e.target.parentNode.insertBefore(dragging, e.target.nextSibling);
	}
}

async function dragEnd() {
	const orderIndex = dragging.dataset.orderIndex;

	const newOrderIndex = Array.prototype.indexOf.call(exercisesList.childNodes, dragging);

	// if order has changed
	if (newOrderIndex != orderIndex) {
		refreshExercisesIndex();
	}

	dragging.classList.remove("dragging");
	dragging = null;
}

function refreshExercisesIndex() {
	for (const [index, exercise] of exercisesList.childNodes.entries()) {
		if (!exercise.dataset.exerciseId) continue;

		exercise.dataset.orderIndex = index;
		exercise.querySelectorAll(".exercise-index").forEach((el) => (el.textContent = index + 1));

		exercises.find(
			(exerciseItem) => exerciseItem.exerciseId == exercise.dataset.exerciseId
		).orderIndex = index;
	}
}

// if not logged in
if (!getJwtTokenInfo()) {
	mainElement.innerHTML = html`
		<h1 class="title">Logga in för att se dina scheman</h1>
		<a href="/login" class="btn primary link">Logga in</a>
	`;

	mainElement.classList.add("row");
	mainElement.style.justifyContent = "center";
} else {
	makeExercisesList();
}
