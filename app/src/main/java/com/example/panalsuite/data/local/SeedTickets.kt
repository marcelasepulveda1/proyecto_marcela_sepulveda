package com.example.panalsuite.data.local

import com.example.panalsuite.data.remote.dto.HistoryDto
import com.example.panalsuite.data.remote.dto.TicketDto

/**
 * Sample tickets used when the app starts without a connection to the API.
 * They are the same records as api/db.json, so both sources stay in sync.
 */
object SeedTickets {

    val all: List<TicketDto> = listOf(
        TicketDto(
            id = "PS-1001",
            title = "Caja 3 no imprime boletas",
            description = "La impresora térmica de la caja 3 no responde desde la apertura. Ya se revisó el papel y el cable.",
            category = "STORE",
            priority = "HIGH",
            status = "OPEN",
            branch = "Sucursal Alameda",
            requesterEmail = "solicitante@panalsuite.test",
            requesterName = "Camila Rojas",
            resolverTeam = "Equipo Tiendas",
            assigneeEmail = null,
            assigneeName = null,
            createdAt = 1792498320000L,
            updatedAt = 1792498320000L,
            history = listOf(
                HistoryDto(null, "OPEN", "Camila Rojas", 1792498320000L)
            )
        ),
        TicketDto(
            id = "PS-1002",
            title = "Stock de paracetamol descuadrado",
            description = "El sistema muestra stock 0 de paracetamol 500 mg, pero en bodega hay 12 cajas sin registrar.",
            category = "PHARMACY",
            priority = "CRITICAL",
            status = "IN_PROGRESS",
            branch = "Sucursal Providencia",
            requesterEmail = "solicitante@panalsuite.test",
            requesterName = "Camila Rojas",
            resolverTeam = "Equipo Farmacia",
            assigneeEmail = "resolutor@panalsuite.test",
            assigneeName = "Diego Fuentes",
            createdAt = 1792435200000L,
            updatedAt = 1792436700000L,
            history = listOf(
                HistoryDto(null, "OPEN", "Camila Rojas", 1792435200000L),
                HistoryDto("OPEN", "IN_PROGRESS", "Diego Fuentes", 1792436700000L)
            )
        ),
        TicketDto(
            id = "PS-1003",
            title = "Despacho de bebidas atrasado",
            description = "El pedido semanal de bebidas no llegó en la fecha acordada y la góndola está casi vacía.",
            category = "SUPPLIER",
            priority = "MEDIUM",
            status = "OPEN",
            branch = "Sucursal Maipú",
            requesterEmail = "solicitante@panalsuite.test",
            requesterName = "Camila Rojas",
            resolverTeam = "Compras y Abastecimiento",
            assigneeEmail = null,
            assigneeName = null,
            createdAt = 1792409400000L,
            updatedAt = 1792409400000L,
            history = listOf(
                HistoryDto(null, "OPEN", "Camila Rojas", 1792409400000L)
            )
        ),
        TicketDto(
            id = "PS-1004",
            title = "Luz de bodega parpadea",
            description = "El tubo fluorescente del pasillo 2 de la bodega parpadea constantemente y molesta al reponer.",
            category = "INFRASTRUCTURE",
            priority = "LOW",
            status = "RESOLVED",
            branch = "Bodega Central",
            requesterEmail = "solicitante@panalsuite.test",
            requesterName = "Camila Rojas",
            resolverTeam = "Mantención",
            assigneeEmail = "resolutor2@panalsuite.test",
            assigneeName = "Valentina Soto",
            createdAt = 1792072800000L,
            updatedAt = 1792182000000L,
            history = listOf(
                HistoryDto(null, "OPEN", "Camila Rojas", 1792072800000L),
                HistoryDto("OPEN", "IN_PROGRESS", "Valentina Soto", 1792084800000L),
                HistoryDto("IN_PROGRESS", "RESOLVED", "Valentina Soto", 1792182000000L)
            )
        ),
        TicketDto(
            id = "PS-1005",
            title = "Contraseña del correo expirada",
            description = "Al iniciar sesión en el correo corporativo aparece el error de contraseña expirada y no deja cambiarla.",
            category = "INTERNAL_SUPPORT",
            priority = "MEDIUM",
            status = "CLOSED",
            branch = "Sucursal Ñuñoa",
            requesterEmail = "solicitante@panalsuite.test",
            requesterName = "Camila Rojas",
            resolverTeam = "Mesa de Ayuda TI",
            assigneeEmail = "resolutor@panalsuite.test",
            assigneeName = "Diego Fuentes",
            createdAt = 1791809100000L,
            updatedAt = 1791904200000L,
            history = listOf(
                HistoryDto(null, "OPEN", "Camila Rojas", 1791809100000L),
                HistoryDto("OPEN", "IN_PROGRESS", "Diego Fuentes", 1791811800000L),
                HistoryDto("IN_PROGRESS", "RESOLVED", "Diego Fuentes", 1791838800000L),
                HistoryDto("RESOLVED", "CLOSED", "Andrea Muñoz", 1791904200000L)
            )
        ),
        TicketDto(
            id = "PS-1006",
            title = "Lector de código de barras falla",
            description = "El lector de la caja 1 no reconoce los productos refrigerados; hay que digitar los códigos a mano.",
            category = "STORE",
            priority = "HIGH",
            status = "IN_PROGRESS",
            branch = "Sucursal Alameda",
            requesterEmail = "solicitante@panalsuite.test",
            requesterName = "Camila Rojas",
            resolverTeam = "Equipo Tiendas",
            assigneeEmail = "resolutor2@panalsuite.test",
            assigneeName = "Valentina Soto",
            createdAt = 1792437900000L,
            updatedAt = 1792497600000L,
            history = listOf(
                HistoryDto(null, "OPEN", "Camila Rojas", 1792437900000L),
                HistoryDto("OPEN", "IN_PROGRESS", "Valentina Soto", 1792497600000L)
            )
        ),
        TicketDto(
            id = "PS-1007",
            title = "Refrigerador de vacunas con alarma",
            description = "El refrigerador de vacunas marca 9 °C y la alarma suena cada 10 minutos.",
            category = "PHARMACY",
            priority = "CRITICAL",
            status = "OPEN",
            branch = "Sucursal Providencia",
            requesterEmail = "solicitante@panalsuite.test",
            requesterName = "Camila Rojas",
            resolverTeam = "Equipo Farmacia",
            assigneeEmail = null,
            assigneeName = null,
            createdAt = 1792504200000L,
            updatedAt = 1792504200000L,
            history = listOf(
                HistoryDto(null, "OPEN", "Camila Rojas", 1792504200000L)
            )
        ),
        TicketDto(
            id = "PS-1008",
            title = "Puerta automática se traba",
            description = "La puerta automática del acceso principal se queda abierta a medio camino en las tardes.",
            category = "INFRASTRUCTURE",
            priority = "MEDIUM",
            status = "RESOLVED",
            branch = "Sucursal Maipú",
            requesterEmail = "solicitante@panalsuite.test",
            requesterName = "Camila Rojas",
            resolverTeam = "Mantención",
            assigneeEmail = "resolutor@panalsuite.test",
            assigneeName = "Diego Fuentes",
            createdAt = 1792253700000L,
            updatedAt = 1792334400000L,
            history = listOf(
                HistoryDto(null, "OPEN", "Camila Rojas", 1792253700000L),
                HistoryDto("OPEN", "IN_PROGRESS", "Diego Fuentes", 1792260000000L),
                HistoryDto("IN_PROGRESS", "RESOLVED", "Diego Fuentes", 1792334400000L)
            )
        )
    )
}
