import { getRequest, getJwtTokenInfo, putRequest, postRequest, deleteRequest } from "../api.js";
import { emptyImg, isElementBefore, secondsToMinutesAndSeconds } from "../utils.js";

const html = String.raw;

const mainElement = document.querySelector("main");

const workoutTemplate = document.querySelector("#workout-template");
const exerciseTemplate = document.querySelector("#workout-exercise-template");
const exerciseSetTemplate = document.querySelector("#exercise-set-template");
const exerciseSetsDropdownTemplate = document.querySelector("#exercise-sets-dropdown-template");
const exerciseSetsDropdownItemTemplate = document.querySelector(
	"#exercise-sets-dropdown-item-template"
);

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
		deleteExercise(exercise, workout, exerciseElement);
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
		.addEventListener("click", async () => {
			const dropdownNodes = exerciseSetsDropdown.querySelectorAll(".exercise-sets-dropdown-item");

			const changedSets = [];

			for (const [index, item] of dropdownNodes.entries()) {
				const reps = item.querySelector(
					".exercise-sets-dropdown-item-reps .exercise-sets-dropdown-item-value"
				);
				const weight = item.querySelector(
					".exercise-sets-dropdown-item-weight .exercise-sets-dropdown-item-value"
				);

				const setNumber = index + 1;

				const set = sets.find((set) => set.setNumber == setNumber);

				if (set.targetReps != reps.value || set.targetWeight != weight.value) {
					changedSets.push({
						setNumber,
						workoutExerciseId: set.workoutExerciseId,
						targetReps: reps.valueAsNumber,
						targetWeight: weight.valueAsNumber,
					});
				}
			}

			if (changedSets.length == 0) {
				exerciseSetsDropdown.classList.toggle("hidden");
				return;
			}

			for (const set of changedSets) {
				await putRequest(`/workoutExercises/${set.workoutExerciseId}/sets/${set.setNumber}`, {
					targetReps: set.targetReps,
					targetWeight: set.targetWeight,
				});

				exerciseSetsDropdown.classList.toggle("hidden");

				const item = exerciseSets.querySelector(
					`.exercise-set[data-set-number="${set.setNumber}"]`
				);

				item.querySelector(".exercise-set-reps").textContent = set.targetReps;
				item.querySelector(".exercise-set-weight").textContent = `${set.targetWeight} kg`;
			}
		});

	exerciseSets.querySelector(".exercise-sets-dropdown-btn").addEventListener("click", () => {
		exerciseSetsDropdown.classList.toggle("hidden");
	});

	return exerciseSetsDropdown;
}

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

		const response = await putRequest(`/workoutExercises/${exercise.workoutExerciseId}/restTime`, {
			restTime,
		});

		if (response.error) {
			alert(response.error);
			return;
		}

		revert(restTime);
	});
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

	const workoutId = addExerciseModal.dataset.workoutId;

	const sets = Array.from({ length: exerciseSets.valueAsNumber }).map((_, i) => ({
		setNumber: i + 1,
		targetReps: exerciseReps.valueAsNumber,
		targetWeight: exerciseWeight.valueAsNumber,
	}));

	const response = await postRequest(`/workouts/${workoutId}/exercises`, {
		exerciseId: +exerciseInput.dataset.exerciseId,
		restTime: isNaN(restTime) ? undefined : restTime,
		sets,
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
	addExerciseModal.querySelector("#exercise-weight").value = "";

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
