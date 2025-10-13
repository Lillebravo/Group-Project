import { postRequest, setJwtToken } from "../api.js";

document.addEventListener("DOMContentLoaded", () => {
	const form = document.getElementById("loginForm");
	const emailInput = document.getElementById("email");
	const passInput = document.getElementById("password");
	const rememberBox = document.getElementById("remember");
	const forgotLink = document.getElementById("forgot");
	const modal = document.getElementById("forgotModal");
	const closeModal = document.getElementById("closeModal");
	const sendReset = document.getElementById("sendReset");
	const resetEmail = document.getElementById("resetEmail");

	/* ---------- 1. KOM IHÅG MIG ------------- */
	const savedEmail = localStorage.getItem("rememberedEmail");
	if (savedEmail) {
		emailInput.value = savedEmail;
		rememberBox.checked = true;
	}

	/* ---------- 2. FORM SUBMIT ------------- */
	form.addEventListener("submit", async (e) => {
		e.preventDefault();
		const email = emailInput.value.trim();
		const password = passInput.value;

		if (!email || !password) {
			alert("Fill in both email and password.");
			return;
		}

		if (rememberBox.checked) {
			localStorage.setItem("rememberedEmail", email);
		} else {
			localStorage.removeItem("rememberedEmail");
		}

		const btn = form.querySelector(".btn.primary");
		btn.disabled = true;
		btn.textContent = "Logging in...";

		const response = await postRequest("/auth/login", { email, password });

		btn.disabled = false;
		btn.textContent = "Sign in";

		if (response.error) {
			alert(response.error);
			return;
		}

		setJwtToken(response.type, response.token);

		window.location.href = "/";
	});

	/* ---------- 3. GLÖMT LÖSENORD (modal) ------------- */
	forgotLink.addEventListener("click", (e) => {
		e.preventDefault();
		modal.classList.remove("hidden");
	});

	closeModal.addEventListener("click", () => {
		modal.classList.add("hidden");
		resetEmail.value = "";
	});

	sendReset.addEventListener("click", () => {
		const email = resetEmail.value.trim();
		if (!email) {
			alert("Please enter a valid email address.");
			return;
		}
		modal.classList.add("hidden");
		resetEmail.value = "";
		alert(`Recovery link sent to ${email}.`);
	});

	/* ---------- 4. REGISTRERING (modal) ------------- */
	const signupLink = document.querySelector(".signup .link.strong");
	const registerModal = document.getElementById("registerModal");
	const closeRegisterModal = document.getElementById("closeRegisterModal");
	const submitRegister = document.getElementById("submitRegister");

	signupLink.addEventListener("click", (e) => {
		e.preventDefault();
		registerModal.classList.remove("hidden");
	});

	closeRegisterModal.addEventListener("click", () => {
		registerModal.classList.add("hidden");
		clearRegisterFields();
	});

	function clearRegisterFields() {
		document.getElementById("regEmail").value = "";
		document.getElementById("regPassword").value = "";
	}

	submitRegister.addEventListener("click", async () => {
		const email = document.getElementById("regEmail").value.trim();
		const password = document.getElementById("regPassword").value;

		if (!email || !password) {
			alert("Fill in all fields to create an account.");
			return;
		}

		const response = await postRequest("/auth/register", { email, password });

		if (response) {
			alert(response.error || response);
			return;
		}

		registerModal.classList.add("hidden");
		clearRegisterFields();
		alert(`Account created for ${email}.`);
	});
});
