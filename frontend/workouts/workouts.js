import { getRequest, getJwtTokenInfo, putRequest } from "../api.js";

const html = String.raw;

const mainElement = document.querySelector("main");
const workoutTemplate = document.querySelector("#workout-template");
const exerciseTemplate = document.querySelector("#workout-exercise-template");

let dragging = null;

const emptyImg = new Image();
emptyImg.src = "data:image/gif;base64,R0lGODlhAQABAIAAAAUEBAAAACwAAAAAAQABAAACAkQBADs=";

async function makeWorkoutsList() {
	const workouts = await getRequest("/workouts/user");

	const workoutsList = document.querySelector("#workouts-list");

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

		workoutsList.appendChild(workoutElement);
	}
}

function makeWorkout(workout) {
	const workoutElement = workoutTemplate.content.cloneNode(true).querySelector(".workout-wrapper");

	workoutElement.dataset.id = workout.workoutId;

	workoutElement.querySelector(".workout-name").textContent = workout.name;
	workoutElement.querySelector(
		".exercise-count"
	).textContent = `${workout.exerciseCount} exercises`;

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

	return exerciseElement;
}

function dragStart(e) {
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

	if (isBefore(dragging, e.target)) {
		e.target.parentNode.insertBefore(dragging, e.target);
	} else {
		e.target.parentNode.insertBefore(dragging, e.target.nextSibling);
	}
}

async function dragEnd(e) {
	const workoutId = dragging.dataset.workoutId;
	const workoutExerciseId = dragging.dataset.workoutExerciseId;
	const orderIndex = dragging.dataset.workoutExerciseId;

	const exercises = document.querySelectorAll(`.exercise[data-workout-id="${workoutId}"]`);

	const newOrderIndex = Array.prototype.indexOf.call(exercises, dragging);

	// if order hasn't changed
	if (newOrderIndex === orderIndex) return;

	await putRequest(`/workouts/${dragging.dataset.workoutId}/exercises/reorder`, {
		workoutExerciseId,
		newOrderIndex,
	});

	for (const [index, exercise] of exercises.entries()) {
		exercise.dataset.orderIndex = index;
		exercise.querySelectorAll(".exercise-index").forEach((el) => (el.textContent = index + 1));
	}

	dragging.classList.remove("dragging");
	dragging = null;
}

function isBefore(el1, el2) {
	let cur;
	if (el2.parentNode === el1.parentNode) {
		for (cur = el1.previousSibling; cur; cur = cur.previousSibling) {
			if (cur === el2) return true;
		}
	}
	return false;
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
