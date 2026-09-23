const API = "http://localhost:8080";

const demoItems = [
  {
    itemId: 1,
    title: "Engineering Mathematics Book",
    description: "Useful first-year engineering mathematics book.",
    category: "Books",
    price: 350,
    condition: "Good"
  },
  {
    itemId: 2,
    title: "Scientific Calculator",
    description: "Working perfectly and lightly used.",
    category: "Electronics",
    price: 600,
    condition: "Good"
  },
  {
    itemId: 3,
    title: "Drawing Sheet Set",
    description: "Engineering drawing sheets, lightly used.",
    category: "Stationery",
    price: 180,
    condition: "Good"
  },
  {
    itemId: 4,
    title: "College Backpack",
    description: "Clean and durable backpack for everyday college use.",
    category: "Accessories",
    price: 450,
    condition: "Good"
  }
];

async function loadHomeItems() {
  const container = document.getElementById("dashboardItems");
  const message = document.getElementById("dashboardMessage");

  if (!container || !message) return;

  let items = [];

  try {
    const response = await fetch(API + "/items");
    if (response.ok) {
      const result = await response.json();
      if (Array.isArray(result)) {
        items = result;
      }
    }
  } catch (error) {
    // Demo items are shown if the backend is not running yet.
  }

  if (items.length === 0) {
    items = demoItems;
    message.textContent = "Demo items are shown below. Login to continue to the marketplace.";
  } else {
    message.textContent = "Browse items without logging in. Login to continue.";
  }

  items.slice(0, 4).forEach(item => {
    const card = document.createElement("div");
    card.className = "preview-card";

    card.innerHTML =
      '<div class="item-image">ITEM</div>' +
      '<div class="item-info">' +
      '<small>' + escapeHtml(item.category) + '</small>' +
      '<h3>' + escapeHtml(item.title) + '</h3>' +
      '<p class="item-description">' + escapeHtml(item.description || "") + '</p>' +
      '<b>₹' + Number(item.price).toLocaleString("en-IN") + '</b>' +
      '<div class="item-condition">' + escapeHtml(item.condition || "") + '</div>' +
      '</div>';

    // Items are publicly visible. Login is only needed to continue/use marketplace features.
    card.addEventListener("click", function () {
      window.location.href = "login.html";
    });

    container.appendChild(card);
  });
}

function escapeHtml(value) {
  return String(value)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

loadHomeItems();
