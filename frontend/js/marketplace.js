const API = "http://localhost:8080";

async function loadItems() {
  const container = document.getElementById("items");

  try {
    const response = await fetch(API + "/items");
    const items = await response.json();

    items.forEach(item => {
      const card = document.createElement("article");
      card.className = "card";
      card.innerHTML =
        '<div class="item-image">ITEM IMAGE</div>' +
        '<div class="item-info">' +
        '<small>' + item.category + '</small>' +
        '<h2>' + item.title + '</h2>' +
        '<p>' + item.description + '</p>' +
        '<div class="item-bottom"><b>₹' + item.price + '</b><span>' + item.condition + '</span></div>' +
        '</div>';
      container.appendChild(card);
    });
  } catch (error) {
    container.innerHTML = "<p>Cannot connect to the Java backend.</p>";
  }
}

loadItems();
