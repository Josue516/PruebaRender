document.addEventListener("DOMContentLoaded", () => {

  const container = document.querySelector("[data-tab]");
  const defaultTab = container?.dataset.tab || "perfil";

  const buttons = document.querySelectorAll(".tab-btn");
  const contents = document.querySelectorAll(".tab-content");

  function setActiveStyles(button) {
    button.classList.add("bg-white", "text-blue-600", "shadow-md");
    button.classList.remove("text-blue-900");
  }

  function setInactiveStyles(button) {
    button.classList.remove("bg-white", "text-blue-600", "shadow-md");
    button.classList.add("text-blue-900");
  }

  function changeTab(tabId) {

    contents.forEach(content => {
      content.hidden = true;
    });

    const activeContent = document.getElementById(tabId);
    if (activeContent) activeContent.hidden = false;

    buttons.forEach(btn => {
      if (btn.dataset.tab === tabId) {
        setActiveStyles(btn);
      } else {
        setInactiveStyles(btn);
      }
    });
  }

  buttons.forEach(btn => {
    btn.addEventListener("click", () => {
      changeTab(btn.dataset.tab);
    });
  });

  changeTab(defaultTab);

});
