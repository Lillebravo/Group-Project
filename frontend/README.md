# Frontend

## Checklist for new pages

Mark a page's JS as a module in HTML, so we can [import functions](#shared-functionality):

```html
<script src="login.js" type="module"></script>
```

Always import `global.css` in your HTML file:

```html
<link rel="stylesheet" href="global.css" />
```

## Shared functionality

Import individual function from global (or "shared") files, for example:

```js
import { getRequest } from "../api.js";

const response = await getRequest("/users");
console.log(response);
```
