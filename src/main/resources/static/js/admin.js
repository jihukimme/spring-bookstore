// Admin panel JavaScript functionality

// Table sorting functionality
function sortTable(columnIndex, tableId = "dataTable") {
  const table = document.getElementById(tableId)
  const tbody = table.querySelector("tbody")
  const rows = Array.from(tbody.querySelectorAll("tr"))

  // Determine sort direction
  const currentSort = table.dataset.sortColumn
  const currentDirection = table.dataset.sortDirection || "asc"
  const newDirection = currentSort == columnIndex && currentDirection === "asc" ? "desc" : "asc"

  // Sort rows
  rows.sort((a, b) => {
    const aValue = a.cells[columnIndex].textContent.trim()
    const bValue = b.cells[columnIndex].textContent.trim()

    // Try to parse as numbers
    const aNum = Number.parseFloat(aValue.replace(/[^\d.-]/g, ""))
    const bNum = Number.parseFloat(bValue.replace(/[^\d.-]/g, ""))

    let comparison = 0
    if (!isNaN(aNum) && !isNaN(bNum)) {
      comparison = aNum - bNum
    } else {
      comparison = aValue.localeCompare(bValue)
    }

    return newDirection === "asc" ? comparison : -comparison
  })

  // Update table
  rows.forEach((row) => tbody.appendChild(row))

  // Update sort indicators
  table.dataset.sortColumn = columnIndex
  table.dataset.sortDirection = newDirection

  // Update header indicators
  const headers = table.querySelectorAll("th")
  headers.forEach((header, index) => {
    header.classList.remove("sort-asc", "sort-desc")
    if (index === columnIndex) {
      header.classList.add(`sort-${newDirection}`)
    }
  })
}

// Bulk actions
function toggleSelectAll(checkbox) {
  const checkboxes = document.querySelectorAll('input[name="selectedItems"]')
  checkboxes.forEach((cb) => {
    cb.checked = checkbox.checked
  })
  updateBulkActionButtons()
}

function updateBulkActionButtons() {
  const selectedItems = document.querySelectorAll('input[name="selectedItems"]:checked')
  const bulkActionButtons = document.querySelectorAll(".bulk-action-btn")

  bulkActionButtons.forEach((btn) => {
    btn.disabled = selectedItems.length === 0
  })

  // Update select all checkbox
  const selectAllCheckbox = document.getElementById("selectAll")
  const allCheckboxes = document.querySelectorAll('input[name="selectedItems"]')

  if (selectAllCheckbox) {
    selectAllCheckbox.indeterminate = selectedItems.length > 0 && selectedItems.length < allCheckboxes.length
    selectAllCheckbox.checked = selectedItems.length === allCheckboxes.length && allCheckboxes.length > 0
  }
}

// Delete confirmation
function confirmDelete(itemName, deleteUrl) {
  if (confirm(`정말로 "${itemName}"을(를) 삭제하시겠습니까?`)) {
    const form = document.createElement("form")
    form.method = "POST"
    form.action = deleteUrl

    const csrfToken = document.querySelector('meta[name="_csrf"]')
    if (csrfToken) {
      const csrfInput = document.createElement("input")
      csrfInput.type = "hidden"
      csrfInput.name = "_csrf"
      csrfInput.value = csrfToken.getAttribute("content")
      form.appendChild(csrfInput)
    }

    document.body.appendChild(form)
    form.submit()
  }
}

// Bulk delete
function bulkDelete() {
  const selectedItems = document.querySelectorAll('input[name="selectedItems"]:checked')

  if (selectedItems.length === 0) {
    alert("삭제할 항목을 선택해주세요.")
    return
  }

  if (confirm(`선택한 ${selectedItems.length}개 항목을 삭제하시겠습니까?`)) {
    const form = document.createElement("form")
    form.method = "POST"
    form.action = "/admin/bulk-delete"

    selectedItems.forEach((item) => {
      const input = document.createElement("input")
      input.type = "hidden"
      input.name = "ids"
      input.value = item.value
      form.appendChild(input)
    })

    const csrfToken = document.querySelector('meta[name="_csrf"]')
    if (csrfToken) {
      const csrfInput = document.createElement("input")
      csrfInput.type = "hidden"
      csrfInput.name = "_csrf"
      csrfInput.value = csrfToken.getAttribute("content")
      form.appendChild(csrfInput)
    }

    document.body.appendChild(form)
    form.submit()
  }
}

// Status update
function updateStatus(itemId, newStatus, updateUrl) {
  const form = document.createElement("form")
  form.method = "POST"
  form.action = updateUrl

  const idInput = document.createElement("input")
  idInput.type = "hidden"
  idInput.name = "id"
  idInput.value = itemId

  const statusInput = document.createElement("input")
  statusInput.type = "hidden"
  statusInput.name = "status"
  statusInput.value = newStatus

  form.appendChild(idInput)
  form.appendChild(statusInput)

  const csrfToken = document.querySelector('meta[name="_csrf"]')
  if (csrfToken) {
    const csrfInput = document.createElement("input")
    csrfInput.type = "hidden"
    csrfInput.name = "_csrf"
    csrfInput.value = csrfToken.getAttribute("content")
    form.appendChild(csrfInput)
  }

  document.body.appendChild(form)
  form.submit()
}

// Search and filter functionality
function applyFilters() {
  const form = document.getElementById("filterForm")
  if (form) {
    form.submit()
  }
}

function resetFilters() {
  const form = document.getElementById("filterForm")
  if (form) {
    const inputs = form.querySelectorAll("input, select")
    inputs.forEach((input) => {
      if (input.type === "checkbox" || input.type === "radio") {
        input.checked = false
      } else {
        input.value = ""
      }
    })
    form.submit()
  }
}

// Initialize admin functionality
document.addEventListener("DOMContentLoaded", () => {
  // Initialize bulk action checkboxes
  const selectAllCheckbox = document.getElementById("selectAll")
  if (selectAllCheckbox) {
    selectAllCheckbox.addEventListener("change", function () {
      toggleSelectAll(this)
    })
  }

  const itemCheckboxes = document.querySelectorAll('input[name="selectedItems"]')
  itemCheckboxes.forEach((checkbox) => {
    checkbox.addEventListener("change", updateBulkActionButtons)
  })

  // Initialize table sorting
  const sortableHeaders = document.querySelectorAll('th[data-sortable="true"]')
  sortableHeaders.forEach((header, index) => {
    header.style.cursor = "pointer"
    header.addEventListener("click", () => {
      sortTable(index)
    })
  })

  // Initialize filter form
  const filterInputs = document.querySelectorAll("#filterForm input, #filterForm select")
  filterInputs.forEach((input) => {
    if (input.type !== "submit" && input.type !== "button") {
      input.addEventListener("change", () => {
        // Auto-submit on filter change (optional)
        // applyFilters();
      })
    }
  })

  // Initialize tooltips if Bootstrap is available
  if (typeof bootstrap !== "undefined") {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    tooltipTriggerList.map((tooltipTriggerEl) => new bootstrap.Tooltip(tooltipTriggerEl))
  }

  // Update bulk action buttons on page load
  updateBulkActionButtons()
})
