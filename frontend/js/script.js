const API = "http://localhost:8080";

const registerForm = document.getElementById("registerForm");
const loginForm = document.getElementById("loginForm");

if (registerForm) {
  registerForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const message = document.getElementById("message");
    const data = {
      name: document.getElementById("name").value.trim(),
      email: document.getElementById("email").value.trim(),
      password: document.getElementById("password").value,
      role: document.getElementById("role").value
    };

    try {
      const response = await fetch(API + "/register", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
      });

      const result = await response.json();
      message.textContent = result.message;

      if (result.success) {
        setTimeout(() => window.location.href = "login.html", 700);
      }
    } catch (error) {
      message.textContent = "Cannot connect to the Java backend.";
    }
  });
}

if (loginForm) {
  loginForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const message = document.getElementById("message");
    const data = {
      email: document.getElementById("email").value.trim(),
      password: document.getElementById("password").value
    };

    try {
      const response = await fetch(API + "/login", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
      });

      const result = await response.json();
      message.textContent = result.message;

      if (result.success) {
        localStorage.setItem("lifeoraUser", JSON.stringify(result.user));
        window.location.href = "marketplace.html";
      }
    } catch (error) {
      message.textContent = "Cannot connect to the Java backend.";
    }
  });
}
