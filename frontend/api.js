const BASE_URL = "http://localhost:8080/api";

// Shouldn't actually be saved in localStorage, but it's the most convenient for now
export function getJwtTokenInfo() {
	return JSON.parse(localStorage.getItem("token"));
}

export function setJwtToken(type, token) {
	localStorage.setItem("token", JSON.stringify({ type, token }));
}

/**
 * Sends a GET request to the specified URL with optional data.
 * @param {string} url The URL to send the request to (e.g. "/users/1").
 * @returns {Promise} A promise that resolves to the response data.
 */
export async function getRequest(url) {
	return baseFetch(url, null, "GET");
}

/**
 * Sends a POST request to the specified URL with optional data.
 * @param {string} url The URL to send the request to (e.g. "/users").
 * @param {object?} data Optional data to send with the request.
 * @returns {Promise} A promise that resolves to the response data.
 */
export async function postRequest(url, data) {
	return baseFetch(url, data, "POST");
}

/**
 * Sends a PUT request to the specified URL with optional data.
 * @param {string} url The URL to send the request to (e.g. "/users/1").
 * @param {object?} data Optional data to send with the request.
 * @returns {Promise} A promise that resolves to the response data.
 */
export async function putRequest(url, data) {
	return baseFetch(url, data, "PUT");
}

/**
 * Sends a DELETE request to the specified URL with optional data.
 * @param {string} url The URL to send the request to (e.g. "/users/1").
 * @param {object?} data Optional data to send with the request.
 * @returns {Promise} A promise that resolves to the response data.
 */
export async function deleteRequest(url, data) {
	return baseFetch(url, data, "DELETE");
}

async function baseFetch(url, data, method) {
	const jwt = getJwtTokenInfo();
	const options = {
		method,
		headers: {
			"Content-Type": "application/json",
		},
	};

	if (jwt && jwt.type === "Bearer") {
		options.headers.Authorization = `${jwt.type} ${jwt.token}`;
	}

	if (data) options.body = JSON.stringify(data);

	return fetch(BASE_URL + url, options).then((res) =>
		res.headers.get("content-type").includes("application/json") ? res.json() : res.text()
	);
}
