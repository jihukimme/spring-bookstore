// Common JavaScript functions for BookStore24

// User ID duplicate check function
function checkUserIdDuplicate() {
  const userId = document.getElementById("userId").value

  if (!userId) {
    alert("아이디를 입력해주세요.")
    return
  }

  if (userId.length < 4 || !/^[a-zA-Z0-9]+$/.test(userId)) {
    alert("4자 이상의 영문, 숫자만 사용 가능합니다.")
    return
  }

  fetch(`/check-userid?userId=${userId}`)
    .then((response) => response.json())
    .then((available) => {
      const feedback = document.getElementById("userIdFeedback")
      const userIdInput = document.getElementById("userId")
      const checkBtn = document.getElementById("checkUserIdBtn")

      if (available) {
        userIdInput.classList.remove("is-invalid")
        userIdInput.classList.add("is-valid")
        feedback.textContent = "사용 가능한 아이디입니다."
        feedback.classList.remove("invalid-feedback")
        feedback.classList.add("valid-feedback")
        checkBtn.textContent = "확인완료"
        checkBtn.disabled = true
        checkBtn.classList.remove("btn-outline-secondary")
        checkBtn.classList.add("btn-success")
      } else {
        userIdInput.classList.remove("is-valid")
        userIdInput.classList.add("is-invalid")
        feedback.textContent = "이미 사용 중인 아이디입니다."
        feedback.classList.remove("valid-feedback")
        feedback.classList.add("invalid-feedback")
      }

      feedback.style.display = "block"
    })
    .catch((error) => {
      console.error("Error:", error)
      alert("중복 확인 중 오류가 발생했습니다.")
    })
}

// Password confirmation validation
function validatePasswordConfirmation() {
  const password = document.getElementById("password").value
  const confirmPassword = document.getElementById("confirmPassword").value
  const feedback = document.getElementById("passwordFeedback")
  const confirmInput = document.getElementById("confirmPassword")

  if (confirmPassword && password !== confirmPassword) {
    confirmInput.classList.add("is-invalid")
    confirmInput.classList.remove("is-valid")
    feedback.textContent = "비밀번호가 일치하지 않습니다."
    feedback.style.display = "block"
    return false
  } else if (confirmPassword && password === confirmPassword) {
    confirmInput.classList.remove("is-invalid")
    confirmInput.classList.add("is-valid")
    feedback.style.display = "none"
    return true
  }
  return true
}

// Cart quantity update
function updateCartQuantity(cartItemId, quantity) {
  if (quantity < 1) {
    if (confirm("상품을 장바구니에서 삭제하시겠습니까?")) {
      removeFromCart(cartItemId)
    }
    return
  }

  const form = document.createElement("form")
  form.method = "POST"
  form.action = "/cart/update"

  const cartItemIdInput = document.createElement("input")
  cartItemIdInput.type = "hidden"
  cartItemIdInput.name = "cartItemId"
  cartItemIdInput.value = cartItemId

  const quantityInput = document.createElement("input")
  quantityInput.type = "hidden"
  quantityInput.name = "quantity"
  quantityInput.value = quantity

  form.appendChild(cartItemIdInput)
  form.appendChild(quantityInput)
  document.body.appendChild(form)
  form.submit()
}

// Remove item from cart
function removeFromCart(cartItemId) {
  const form = document.createElement("form")
  form.method = "POST"
  form.action = "/cart/remove"

  const cartItemIdInput = document.createElement("input")
  cartItemIdInput.type = "hidden"
  cartItemIdInput.name = "cartItemId"
  cartItemIdInput.value = cartItemId

  form.appendChild(cartItemIdInput)
  document.body.appendChild(form)
  form.submit()
}

// Add to cart
function addToCart(bookId, quantity = 1) {
  const form = document.createElement("form")
  form.method = "POST"
  form.action = "/cart/add"

  const bookIdInput = document.createElement("input")
  bookIdInput.type = "hidden"
  bookIdInput.name = "bookId"
  bookIdInput.value = bookId

  const quantityInput = document.createElement("input")
  quantityInput.type = "hidden"
  quantityInput.name = "quantity"
  quantityInput.value = quantity

  form.appendChild(bookIdInput)
  form.appendChild(quantityInput)
  document.body.appendChild(form)
  form.submit()
}

// Search functionality
function performSearch(query) {
  if (!query.trim()) {
    alert("검색어를 입력해주세요.")
    return
  }

  window.location.href = `/books?title=${encodeURIComponent(query)}`
}

// Initialize common functionality
document.addEventListener("DOMContentLoaded", () => {
  // User ID duplicate check button
  const checkUserIdBtn = document.getElementById("checkUserIdBtn")
  if (checkUserIdBtn) {
    checkUserIdBtn.addEventListener("click", checkUserIdDuplicate)
  }

  // Password confirmation validation
  const confirmPasswordInput = document.getElementById("confirmPassword")
  if (confirmPasswordInput) {
    confirmPasswordInput.addEventListener("input", validatePasswordConfirmation)
  }

  // Search form submission
  const searchForms = document.querySelectorAll('form[action*="/books"]')
  searchForms.forEach((form) => {
    form.addEventListener("submit", (e) => {
      const searchInput = form.querySelector('input[name="title"]')
      if (searchInput && !searchInput.value.trim()) {
        e.preventDefault()
        alert("검색어를 입력해주세요.")
        searchInput.focus()
      }
    })
  })

  // Quantity input validation
  const quantityInputs = document.querySelectorAll('input[type="number"][name="quantity"]')
  quantityInputs.forEach((input) => {
    input.addEventListener("change", function () {
      const value = Number.parseInt(this.value)
      if (value < 1) {
        this.value = 1
      } else if (value > 99) {
        this.value = 99
      }
    })
  })

  // Auto-hide alerts after 5 seconds
  const alerts = document.querySelectorAll(".alert")
  alerts.forEach((alert) => {
    if (alert.classList.contains("alert-success") || alert.classList.contains("alert-info")) {
      setTimeout(() => {
        alert.style.transition = "opacity 0.5s"
        alert.style.opacity = "0"
        setTimeout(() => {
          alert.remove()
        }, 500)
      }, 5000)
    }
  })
})
