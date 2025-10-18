export function isElementBefore(el1, el2) {
	let cur;
	if (el2.parentNode === el1.parentNode) {
		for (cur = el1.previousSibling; cur; cur = cur.previousSibling) {
			if (cur === el2) return true;
		}
	}
	return false;
}

export const emptyImg = new Image();
emptyImg.src = "data:image/gif;base64,R0lGODlhAQABAIAAAAUEBAAAACwAAAAAAQABAAACAkQBADs=";
