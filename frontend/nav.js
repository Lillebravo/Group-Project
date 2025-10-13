import { getJwtTokenInfo, postRequest } from "./api.js";

const signInBtn = document.querySelector("#signInBtn");

const jwt = getJwtTokenInfo();

async function signOut(e) {
	e.preventDefault();

	const confirmation = confirm("Är du säker på att du vill logga ut?");
	if (!confirmation) return;

	await postRequest("/auth/logout");
	localStorage.removeItem("token");
	window.location.href = "/";
}

if (!jwt) {
	signInBtn.textContent = "Bli medlem";
	signInBtn.href = "/login";
	signInBtn.removeEventListener("click", signOut);
} else {
	signInBtn.textContent = "Logga ut";
	signInBtn.href = "/";
	signInBtn.addEventListener("click", signOut);
}
