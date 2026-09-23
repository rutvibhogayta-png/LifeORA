const API = "http://localhost:8080";
const user = JSON.parse(localStorage.getItem("lifeoraUser"));
const welcomeName = document.getElementById("welcomeName");
const isDashboardPage = !!welcomeName;

// The main index page shows a public preview.
// The separate dashboard page requires login.
if (isDashboardPage && !user) {
  window.location.href = "login.html";
}

if (user && welcomeName) {
  welcomeName.textContent = "Welcome, " + user.name;
}

async function loadDashboardItems() {
  const container = document.getElementById("dashboardItems");
  const message = document.getElementById("dashboardMessage");

  if (!container || !message) return;

  try {
    const response = await fetch(API + "/items");
    const items = await response.json();

    if (!Array.isArray(items) || items.length === 0) {
      message.textContent = "No items available yet.";
      return;
    }

    items.slice(0, 3).forEach(item => {
      const card = document.createElement("div");
      card.className = "preview-card";
      card.innerHTML =
        '<div class="item-image">ITEM IMAGE</div>' +
        '<div class="item-info">' +
        '<small>' + item.category + '</small>' +
        '<h3>' + item.title + '</h3>' +
        '<b>₹' + item.price + '</b>' +
        '</div>';

      card.addEventListener("click", function () {
        window.location.href = "login.html";
      });

      container.appendChild(card);
    });

    message.textContent = "Click any item to continue to the login page.";
  } catch (error) {
    message.textContent = "Cannot load items from the Java backend. Make sure the backend is running.";
  }
}

loadDashboardItems();
