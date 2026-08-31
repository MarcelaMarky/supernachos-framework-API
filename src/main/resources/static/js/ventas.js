/*
 * Formulario de ventas: agrega y quita filas de productos y calcula el total
 * estimado en pantalla. El calculo definitivo lo hace el servidor.
 */
(function () {
	const cuerpo = document.getElementById('cuerpoLineas');
	const btnAgregar = document.getElementById('btnAgregar');
	const totalGeneral = document.getElementById('totalGeneral');
	if (!cuerpo) {
		return;
	}

	function formatear(valor) {
		return '$ ' + valor.toFixed(2);
	}

	function precioSeleccionado(select) {
		const opcion = select.options[select.selectedIndex];
		if (!opcion) {
			return 0;
		}
		return parseFloat(opcion.getAttribute('data-precio') || '0');
	}

	function recalcular() {
		let total = 0;
		cuerpo.querySelectorAll('tr.linea').forEach(function (fila) {
			const select = fila.querySelector('.selProducto');
			const cantidad = parseInt(fila.querySelector('.txtCantidad').value, 10);
			const subtotal = (select.value && cantidad > 0) ? precioSeleccionado(select) * cantidad : 0;
			fila.querySelector('.subtotal').textContent = formatear(subtotal);
			total += subtotal;
		});
		totalGeneral.textContent = formatear(total);
	}

	/* Renumera los name/id para que Spring pueda enlazar la lista lineas[i]. */
	function renumerar() {
		cuerpo.querySelectorAll('tr.linea').forEach(function (fila, indice) {
			fila.querySelectorAll('select, input').forEach(function (campo) {
				if (campo.name) {
					campo.name = campo.name.replace(/lineas\[\d+\]/, 'lineas[' + indice + ']');
				}
				if (campo.id) {
					campo.id = campo.id.replace(/lineas\d+\./, 'lineas' + indice + '.');
				}
			});
		});
	}

	btnAgregar.addEventListener('click', function () {
		const filas = cuerpo.querySelectorAll('tr.linea');
		const nueva = filas[filas.length - 1].cloneNode(true);
		nueva.querySelector('.selProducto').selectedIndex = 0;
		nueva.querySelector('.txtCantidad').value = '';
		nueva.querySelector('.subtotal').textContent = '$ 0.00';
		cuerpo.appendChild(nueva);
		renumerar();
	});

	cuerpo.addEventListener('click', function (evento) {
		if (!evento.target.classList.contains('quitar')) {
			return;
		}
		if (cuerpo.querySelectorAll('tr.linea').length === 1) {
			const fila = evento.target.closest('tr.linea');
			fila.querySelector('.selProducto').selectedIndex = 0;
			fila.querySelector('.txtCantidad').value = '';
		}
		else {
			evento.target.closest('tr.linea').remove();
		}
		renumerar();
		recalcular();
	});

	cuerpo.addEventListener('change', recalcular);
	cuerpo.addEventListener('input', recalcular);
	recalcular();
})();
