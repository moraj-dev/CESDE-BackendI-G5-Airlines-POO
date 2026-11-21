# Módulo 5: Reservas

Este módulo gestiona las reservaciones y los pasajeros con sus sillas, cuida reglas tales como que no debe haber sillas repetidas, documentos duplicados o que se vean datos sensibles importantes de los pasajeros.

## Decisiones de diseño
- Silla: La fila es una letra esta puede ser escrita tanto en mayúscula como en minuscula, y el programa la guarda siempre en mayúscula (así a12 y A12); el numero de las sillas va entre 1 y 99.
- Pasajeros: El máximo de pasajeros que puede tener una reserva es 10; la reserva debe tener como mínimo un pasajero para que no rompa la regla y la reserva no exista inválida.
- Documento: Se guarda en mayúsculas, así ac123 y AC123 es considerado el mismo documento; no puede repetirse un documento en la misma reserva, se aceptan letras, numeros y no debe tener espacios.
- Identificadores: El número (id) de cada pasajero es asignado por el programa, el usuario no lo tiene que escribir este lo entrega IdGenerator y empieza en 1; no tiene setter ya que no cambia después de creado.
