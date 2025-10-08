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
  form.addEventListener("submit", (e) => {
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

    setTimeout(() => {
      btn.disabled = false;
      btn.textContent = "Sign in";
      alert("Login succeeded!");
    }, 1200);
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
    document.getElementById("firstName").value = "";
    document.getElementById("lastName").value = "";
    document.getElementById("regEmail").value = "";
    document.getElementById("regPassword").value = "";
  }

  submitRegister.addEventListener("click", () => {
    const first = document.getElementById("firstName").value.trim();
    const last = document.getElementById("lastName").value.trim();
    const email = document.getElementById("regEmail").value.trim();
    const pass = document.getElementById("regPassword").value;

    if (!first || !last || !email || !pass) {
      alert("Fill in all fields to create an account.");
      return;
    }

    registerModal.classList.add("hidden");
    clearRegisterFields();
    alert(`Account created for ${first} ${last} (${email}).`);
  });
});
