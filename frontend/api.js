const BASE_URL = "http://localhost:8080";

// Will eventually handle JWT tokens

/**
 * Sends a GET request to the specified URL with optional data.
 * @param {string} url The URL to send the request to (e.g. "/users/1").
 * @returns {Promise} A promise that resolves to the response data.
 */
export async function getRequest(url) {
	return fetch(BASE_URL + url, {
		method: "GET",
		headers: {
			"Content-Type": "application/json",
			Accept: "application/json",
		},
	}).then((res) => res.json());
}

/**
 * Sends a POST request to the specified URL with optional data.
 * @param {string} url The URL to send the request to (e.g. "/users").
 * @param {object?} data Optional data to send with the request.
 * @returns {Promise} A promise that resolves to the response data.
 */
export async function postRequest(url, data) {
	return fetch(BASE_URL + url, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
			Accept: "application/json",
		},
		body: JSON.stringify(data),
	}).then((res) => res.json());
}

/**
 * Sends a PUT request to the specified URL with optional data.
 * @param {string} url The URL to send the request to (e.g. "/users/1").
 * @param {object?} data Optional data to send with the request.
 * @returns {Promise} A promise that resolves to the response data.
 */
export async function putRequest(url, data) {
	return fetch(BASE_URL + url, {
		method: "PUT",
		headers: {
			"Content-Type": "application/json",
			Accept: "application/json",
		},
		body: JSON.stringify(data),
	}).then((res) => res.json());
}

/**
 * Sends a DELETE request to the specified URL with optional data.
 * @param {string} url The URL to send the request to (e.g. "/users/1").
 * @param {object?} data Optional data to send with the request.
 * @returns {Promise} A promise that resolves to the response data.
 */
export async function deleteRequest(url, data) {
	return fetch(BASE_URL + url, {
		method: "DELETE",
		headers: {
			"Content-Type": "application/json",
			Accept: "application/json",
		},
		body: JSON.stringify(data),
	}).then((res) => res.json());
}
