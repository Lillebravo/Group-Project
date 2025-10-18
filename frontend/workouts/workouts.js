import { getRequest, getJwtTokenInfo, putRequest, postRequest, deleteRequest } from "../api.js";
import { emptyImg, isElementBefore } from "../utils.js";

const html = String.raw;

const mainElement = document.querySelector("main");
const workoutTemplate = document.querySelector("#workout-template");
const exerciseTemplate = document.querySelector("#workout-exercise-template");
const addExerciseModal = document.querySelector("#add-exercise-modal");
const workoutsList = document.querySelector("#workouts-list");

let dragging = null;
let currentlyEditingExercise = false;

async function makeWorkoutsList() {
	const workouts = await getRequest("/workouts/user");

	// clean up previous workouts
	workoutsList.querySelectorAll(".workout-wrapper").forEach((workout) => workout.remove());

	for (const workout of workouts) {
		const workoutElement = makeWorkout(workout);

		const sortedExercises = workout.exercises.sort((a, b) => a.orderIndex - b.orderIndex);
		const exercisesList = workoutElement.querySelector(".workout-exercises");

		for (const exercise of sortedExercises) {
			const exerciseElement = makeExercise(workout, exercise);
			exercisesList.appendChild(exerciseElement);
		}

		const actionWrapper = document.createElement("div");
		actionWrapper.classList.add("workout-actions");

		const addExerciseBtn = document.createElement("button");
		addExerciseBtn.textContent = "Lägg till övning";
		addExerciseBtn.classList.add("add-workout");
		addExerciseBtn.addEventListener("click", () => {
			addExercise(workout);
		});

		const deleteWorkoutBtn = document.createElement("button");
		deleteWorkoutBtn.textContent = "Ta bort schema";
		deleteWorkoutBtn.classList.add("delete-workout");
		deleteWorkoutBtn.addEventListener("click", () => {
			deleteWorkout(workout);
		});

		actionWrapper.append(addExerciseBtn, deleteWorkoutBtn);
		exercisesList.appendChild(actionWrapper);

		workoutsList.appendChild(workoutElement);
	}

	const addWorkoutBtn = document.createElement("a");
	addWorkoutBtn.href = "/workouts/create";
	addWorkoutBtn.textContent = "Lägg till schema";
	addWorkoutBtn.classList.add("add-workout", "link");
	workoutsList.appendChild(addWorkoutBtn);
}

function makeWorkout(workout) {
	const workoutElement = workoutTemplate.content.cloneNode(true).querySelector(".workout-wrapper");

	workoutElement.dataset.id = workout.workoutId;

	workoutElement.querySelector(".workout-name").textContent = workout.name;
	workoutElement.querySelector(".exercise-count").textContent = `${workout.exerciseCount} övningar`;

	workoutElement.querySelector(".workout").addEventListener("click", (e) => {
		const workoutExercises = workoutElement.querySelector(".workout-exercises");
		const workoutExpand = workoutElement.querySelector(".workout-expand");

		workoutExercises.classList.toggle("active");

		workoutExpand.textContent = workoutExercises.classList.contains("active") ? "▲" : "▼";
	});

	return workoutElement;
}

function makeExercise(workout, exercise) {
	const exerciseElement = exerciseTemplate.content.cloneNode(true).querySelector(".exercise");

	exerciseElement.dataset.workoutId = workout.workoutId;
	exerciseElement.dataset.workoutExerciseId = exercise.workoutExerciseId;
	exerciseElement.dataset.orderIndex = exercise.orderIndex;

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
		deleteExercise(exercise, workout, exerciseElement);
	});

	exerciseElement.querySelector(".edit-exercise").addEventListener("click", () => {
		editExercise(exercise, workout, exerciseElement);
	});

	return exerciseElement;
}

async function deleteWorkout(workout) {
	const confirmation = confirm(`Är du säker på att du vill ta bort ${workout.name}?`);
	if (!confirmation) return;

	const response = await deleteRequest(`/workouts/${workout.workoutId}`);

	if (response.error) {
		alert(response.error);
		return;
	}

	workoutsList.querySelector(`.workout-wrapper[data-id="${workout.workoutId}"]`).remove();
}

async function deleteExercise(exercise, workout, exerciseElement) {
	const confirmation = confirm(
		`Är du säker på att du vill ta bort ${exercise.exerciseName} från ${workout.name}?`
	);
	if (!confirmation) return;

	const response = await deleteRequest(`/workoutExercises/${exercise.workoutExerciseId}`);

	if (response.error) {
		alert(response.error);
		return;
	}

	exerciseElement.remove();

	document.querySelector(
		`.workout-wrapper[data-id="${workout.workoutId}"] .exercise-count`
	).textContent = `${workout.exerciseCount - 1} övningar`;

	const exercises = document.querySelectorAll(`.exercise[data-workout-id="${workout.workoutId}"]`);

	refreshExercisesIndex(exercises);
}

async function editExercise(exercise, workout, exerciseElement) {
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
			editExercise(exercise, workout, exerciseElement);
		});

		oldDeleteBtn.addEventListener("click", () => {
			deleteExercise(exercise, workout, exerciseElement);
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

		const response = await putRequest(
			`/workoutExercises/${workout.workoutId}/exercise/${exercise.exerciseId}`,
			{
				sets,
				reps,
			}
		);

		if (response.error) {
			alert(response.error);
			return;
		}

		newSetsValue.replaceWith(oldSetsValue);
		newRepsValue.replaceWith(oldRepsValue);

		oldSetsValue.textContent = sets;
		oldRepsValue.textContent = reps;

		revertButtons();
		currentlyEditingExercise = false;
	});
}

function addExercise(workout) {
	addExerciseModal.querySelector(
		"#exercise-modal-description"
	).textContent = `Lägg till övning för ${workout.name}`;

	addExerciseModal.dataset.workoutId = workout.workoutId;

	addExerciseModal.classList.remove("hidden");
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

	const workoutId = addExerciseModal.dataset.workoutId;

	const response = await postRequest(`/workouts/${workoutId}/exercises`, {
		exerciseId: +exerciseInput.dataset.exerciseId,
		sets: exerciseSets.valueAsNumber,
		reps: exerciseReps.valueAsNumber,
	});

	if (response.message || response.error) {
		alert(response.message || response.error);
		return;
	}

	const exerciseList = document.querySelector(
		`.workout-wrapper[data-id="${workoutId}"] .workout-exercises`
	);

	const exerciseElement = makeExercise(
		{ workoutId },
		response.exercises[response.exercises.length - 1]
	);

	addExerciseModal.classList.add("hidden");
	addExerciseModal.querySelector("#exercise-input").value = "";
	addExerciseModal.querySelector("#exercise-sets").value = "";
	addExerciseModal.querySelector("#exercise-reps").value = "";

	document.querySelector(
		`.workout-wrapper[data-id="${workoutId}"] .exercise-count`
	).textContent = `${response.exercises.length} övningar`;

	exerciseList.insertBefore(exerciseElement, exerciseList.lastChild);
});

addExerciseModal.querySelector("#close-exercise-modal").addEventListener("click", (e) => {
	e.preventDefault();
	addExerciseModal.classList.add("hidden");
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

async function dragEnd(e) {
	const workoutId = dragging.dataset.workoutId;
	const workoutExerciseId = dragging.dataset.workoutExerciseId;
	const orderIndex = dragging.dataset.orderIndex;

	const exercises = document.querySelectorAll(`.exercise[data-workout-id="${workoutId}"]`);

	const newOrderIndex = Array.prototype.indexOf.call(exercises, dragging);

	// if order hasn't changed
	if (newOrderIndex == orderIndex) {
		dragging.classList.remove("dragging");
		dragging = null;
		return;
	}

	await putRequest(`/workouts/${dragging.dataset.workoutId}/exercises/reorder`, {
		workoutExerciseId,
		newOrderIndex,
	});

	refreshExercisesIndex(exercises);

	dragging.classList.remove("dragging");
	dragging = null;
}

function refreshExercisesIndex(exercises) {
	for (const [index, exercise] of exercises.entries()) {
		exercise.dataset.orderIndex = index;
		exercise.querySelectorAll(".exercise-index").forEach((el) => (el.textContent = index + 1));
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
	makeWorkoutsList();
}
