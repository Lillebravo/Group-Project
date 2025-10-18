import { getJwtTokenInfo, postRequest } from "../../api.js";
import { emptyImg, isElementBefore } from "../../utils.js";

const html = String.raw;

const exercisesList = document.querySelector(".workout-exercises");
const addExerciseModal = document.querySelector("#add-exercise-modal");
const exerciseTemplate = document.querySelector("#workout-exercise-template");

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

	if (
		!exerciseInput.value ||
		!exerciseInput.dataset.exerciseId ||
		!exerciseSets.value ||
		!exerciseReps.value
	) {
		alert("Fyll i alla fält");
		return;
	}

	const newExercise = {
		exerciseId: +exerciseInput.dataset.exerciseId,
		exerciseName: exerciseInput.value,
		muscleGroup: exerciseInput.dataset.muscleGroup,
		sets: exerciseSets.valueAsNumber,
		reps: exerciseReps.valueAsNumber,
		orderIndex: exercises.length,
	};

	exercises.push(newExercise);

	const exerciseElement = makeExercise(newExercise);

	exercisesList.insertBefore(exerciseElement, exercisesList.lastChild);

	addExerciseModal.classList.add("hidden");
	addExerciseModal.querySelector("#exercise-input").value = "";
	addExerciseModal.querySelector("#exercise-sets").value = "";
	addExerciseModal.querySelector("#exercise-reps").value = "";
});

function makeExercise(exercise) {
	const exerciseElement = exerciseTemplate.content.cloneNode(true).querySelector(".exercise");

	exerciseElement.dataset.exerciseId = exercise.exerciseId;

	exerciseElement.addEventListener("dragstart", dragStart);
	exerciseElement.addEventListener("dragenter", dragEnter);
	exerciseElement.addEventListener("dragover", dragOver);
	exerciseElement.addEventListener("dragend", dragEnd);

	exerciseElement.querySelector(".exercise-name").textContent = exercise.exerciseName;
	exerciseElement.querySelector(".exercise-muscle-group").textContent = exercise.muscleGroup;
	exerciseElement.querySelector(".exercise-index").textContent = exercise.orderIndex + 1;

	exerciseElement.querySelector(".exercise-sets").textContent = exercise.sets;
	exerciseElement.querySelector(".exercise-reps").textContent = exercise.reps;

	exerciseElement.querySelector(".delete-exercise").addEventListener("click", () => {
		e.preventDefault();
		deleteExercise(exercise, exerciseElement);
	});

	exerciseElement.querySelector(".edit-exercise").addEventListener("click", (e) => {
		e.preventDefault();
		editExercise(exercise, exerciseElement);
	});

	return exerciseElement;
}

addExerciseModal.querySelector("#close-exercise-modal").addEventListener("click", (e) => {
	e.preventDefault();
	addExerciseModal.classList.add("hidden");
});

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

async function editExercise(exercise, exerciseElement) {
	if (currentlyEditingExercise) return;
	currentlyEditingExercise = true;

	const setsValue = exerciseElement.querySelector(".exercise-sets");
	const repsValue = exerciseElement.querySelector(".exercise-reps");
	const oldSetsValue = setsValue.cloneNode(true);
	const oldRepsValue = repsValue.cloneNode(true);

	const editBtn = exerciseElement.querySelector(".edit-exercise");
	const deleteBtn = exerciseElement.querySelector(".delete-exercise");
	const oldEditBtn = editBtn.cloneNode(true);
	const oldDeleteBtn = deleteBtn.cloneNode(true);

	const newEditBtn = document.createElement("button");
	newEditBtn.classList.add("btn", "small", "cancel-edit");
	newEditBtn.insertAdjacentHTML(
		"afterbegin",
		`<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="trash"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>`
	);

	const newDeleteBtn = document.createElement("button");
	newDeleteBtn.classList.add("btn", "small", "confirm-edit");
	newDeleteBtn.insertAdjacentHTML(
		"afterbegin",
		`<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="confirm"><path d="M20 6 9 17l-5-5"/></svg>`
	);

	editBtn.replaceWith(newEditBtn);
	deleteBtn.replaceWith(newDeleteBtn);

	const newSetsValue = document.createElement("input");
	newSetsValue.type = "number";
	newSetsValue.value = setsValue.textContent;
	newSetsValue.classList.add("input");

	const newRepsValue = document.createElement("input");
	newRepsValue.type = "number";
	newRepsValue.value = repsValue.textContent;
	newRepsValue.classList.add("input");

	setsValue.replaceWith(newSetsValue);
	repsValue.replaceWith(newRepsValue);

	const revertButtons = () => {
		newEditBtn.replaceWith(oldEditBtn);
		newDeleteBtn.replaceWith(oldDeleteBtn);

		// Reattach event listeners
		oldEditBtn.addEventListener("click", () => {
			editExercise(exercise, exerciseElement);
		});

		oldDeleteBtn.addEventListener("click", () => {
			deleteExercise(exercise, exerciseElement);
		});
	};

	// Cancel
	newEditBtn.addEventListener("click", () => {
		newSetsValue.replaceWith(oldSetsValue);
		newRepsValue.replaceWith(oldRepsValue);

		revertButtons();

		currentlyEditingExercise = false;
	});

	// Confirm
	newDeleteBtn.addEventListener("click", async () => {
		const sets = newSetsValue.valueAsNumber;
		const reps = newRepsValue.valueAsNumber;

		if (!sets || !reps) {
			alert("Fyll i alla fält");
			return;
		}

		if (sets < 1 || reps < 1) {
			alert("Sätt minst 1 set och 1 rep");
			return;
		}

		const exerciseItem = exercises.find(
			(exerciseItem) => exerciseItem.exerciseId == exercise.exerciseId
		);
		exerciseItem.sets = sets;
		exerciseItem.reps = reps;

		newSetsValue.replaceWith(oldSetsValue);
		newRepsValue.replaceWith(oldRepsValue);

		oldSetsValue.textContent = sets;
		oldRepsValue.textContent = reps;

		revertButtons();
		currentlyEditingExercise = false;
	});
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
			reps: exercise.reps,
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
