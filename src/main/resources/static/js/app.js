// JavaScript interactivo para el Sistema de Inventario

document.addEventListener('DOMContentLoaded', function() {
    // 1. Manejo del Modal de Entrada de Stock en Inventario
    const entradaModal = document.getElementById('entradaModal');
    if (entradaModal) {
        entradaModal.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            const productoId = button.getAttribute('data-producto-id');
            const productoNombre = button.getAttribute('data-producto-nombre');
            const productoCodigo = button.getAttribute('data-producto-codigo');
            const stockActual = button.getAttribute('data-producto-stock');

            document.getElementById('modalProductoId').value = productoId;
            document.getElementById('modalProductoNombre').textContent = productoNombre + ' (' + productoCodigo + ')';
            document.getElementById('modalStockActual').textContent = stockActual + ' unidades';
            document.getElementById('modalCantidad').value = '';
            document.getElementById('modalMotivo').value = '';
        });
    }

    // 2. Manejo dinámico del formulario de Salida de Stock (Almacenista)
    const selectProductoSalida = document.getElementById('selectProductoSalida');
    const inputCantidadSalida = document.getElementById('inputCantidadSalida');
    const displayStockDisponible = document.getElementById('displayStockDisponible');
    const stockWarning = document.getElementById('stockWarning');
    const btnSubmitSalida = document.getElementById('btnSubmitSalida');

    function actualizarInfoProductoSalida() {
        if (!selectProductoSalida) return;
        const selectedOption = selectProductoSalida.options[selectProductoSalida.selectedIndex];
        if (selectedOption && selectedOption.value) {
            const stock = parseInt(selectedOption.getAttribute('data-stock') || '0', 10);
            const codigo = selectedOption.getAttribute('data-codigo') || '';
            
            if (displayStockDisponible) {
                displayStockDisponible.textContent = stock;
            }
            if (inputCantidadSalida) {
                inputCantidadSalida.max = stock;
                validarCantidadSalida(stock);
            }
        } else {
            if (displayStockDisponible) {
                displayStockDisponible.textContent = '0';
            }
            if (stockWarning) {
                stockWarning.classList.add('d-none');
            }
            if (btnSubmitSalida) {
                btnSubmitSalida.disabled = false;
            }
        }
    }

    function validarCantidadSalida(stockDisponible) {
        if (!inputCantidadSalida) return;
        const cantidad = parseInt(inputCantidadSalida.value || '0', 10);

        if (cantidad > stockDisponible) {
            if (stockWarning) {
                stockWarning.textContent = '¡Atención! La cantidad a retirar (' + cantidad + ') supera el stock disponible en almacén (' + stockDisponible + ').';
                stockWarning.classList.remove('d-none');
            }
            if (btnSubmitSalida) {
                btnSubmitSalida.disabled = true;
            }
        } else {
            if (stockWarning) {
                stockWarning.classList.add('d-none');
            }
            if (btnSubmitSalida) {
                btnSubmitSalida.disabled = false;
            }
        }
    }

    if (selectProductoSalida) {
        selectProductoSalida.addEventListener('change', actualizarInfoProductoSalida);
        actualizarInfoProductoSalida();
    }

    if (inputCantidadSalida && selectProductoSalida) {
        inputCantidadSalida.addEventListener('input', function() {
            const selectedOption = selectProductoSalida.options[selectProductoSalida.selectedIndex];
            const stock = selectedOption ? parseInt(selectedOption.getAttribute('data-stock') || '0', 10) : 0;
            validarCantidadSalida(stock);
        });
    }

    // Auto-dismiss alerts after 6 seconds
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert.alert-dismissible');
        alerts.forEach(function(alert) {
            const bsAlert = bootstrap.Alert.getInstance(alert);
            if (bsAlert) {
                bsAlert.close();
            }
        });
    }, 6000);
});
