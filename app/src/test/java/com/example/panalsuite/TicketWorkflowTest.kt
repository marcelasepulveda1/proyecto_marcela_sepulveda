package com.example.panalsuite

import com.example.panalsuite.model.Category
import com.example.panalsuite.model.Priority
import com.example.panalsuite.model.Role
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketAction
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.model.TicketWorkflow
import com.example.panalsuite.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TicketWorkflowTest {

    private val requester = User("solicitante@panalsuite.test", "Camila Rojas", Role.REQUESTER)
    private val otherRequester = User("otro@panalsuite.test", "Pedro Pérez", Role.REQUESTER)
    private val resolver = User("resolutor@panalsuite.test", "Diego Fuentes", Role.RESOLVER)
    private val otherResolver = User("resolutor2@panalsuite.test", "Valentina Soto", Role.RESOLVER)
    private val supervisor = User("supervisor@panalsuite.test", "Andrea Muñoz", Role.SUPERVISOR)

    private fun ticket(status: TicketStatus, assignee: User? = null) = Ticket(
        id = "PS-0001",
        title = "Caja no imprime",
        description = "La impresora de la caja no responde",
        category = Category.STORE,
        priority = Priority.HIGH,
        status = status,
        branch = "Sucursal Alameda",
        requesterEmail = requester.email,
        requesterName = requester.name,
        resolverTeam = Category.STORE.team,
        assigneeEmail = assignee?.email,
        assigneeName = assignee?.name,
        createdAt = 0L,
        updatedAt = 0L
    )

    @Test
    fun requester_onlySeesOwnTickets() {
        assertTrue(TicketWorkflow.isVisibleTo(ticket(TicketStatus.OPEN), requester))
        assertFalse(TicketWorkflow.isVisibleTo(ticket(TicketStatus.OPEN), otherRequester))
    }

    @Test
    fun resolver_seesOpenAndAssignedTickets_only() {
        assertTrue(TicketWorkflow.isVisibleTo(ticket(TicketStatus.OPEN), resolver))
        assertTrue(TicketWorkflow.isVisibleTo(ticket(TicketStatus.IN_PROGRESS, resolver), resolver))
        assertFalse(TicketWorkflow.isVisibleTo(ticket(TicketStatus.IN_PROGRESS, otherResolver), resolver))
    }

    @Test
    fun supervisor_seesEverything() {
        assertTrue(TicketWorkflow.isVisibleTo(ticket(TicketStatus.CLOSED, otherResolver), supervisor))
    }

    @Test
    fun resolver_canTakeOpenTicket() {
        assertEquals(listOf(TicketAction.TAKE), TicketWorkflow.actionsFor(ticket(TicketStatus.OPEN), resolver))
    }

    @Test
    fun resolver_canOnlyResolveOwnTicket() {
        assertEquals(
            listOf(TicketAction.RESOLVE),
            TicketWorkflow.actionsFor(ticket(TicketStatus.IN_PROGRESS, resolver), resolver)
        )
        assertTrue(TicketWorkflow.actionsFor(ticket(TicketStatus.IN_PROGRESS, otherResolver), resolver).isEmpty())
    }

    @Test
    fun requester_canCloseOrReopenResolvedTicket() {
        assertEquals(
            listOf(TicketAction.CLOSE, TicketAction.REOPEN),
            TicketWorkflow.actionsFor(ticket(TicketStatus.RESOLVED, resolver), requester)
        )
    }

    @Test
    fun requester_hasNoActionsOnOpenTicket() {
        assertTrue(TicketWorkflow.actionsFor(ticket(TicketStatus.OPEN), requester).isEmpty())
    }

    @Test
    fun supervisor_canReopenClosedTicket() {
        assertEquals(listOf(TicketAction.REOPEN), TicketWorkflow.actionsFor(ticket(TicketStatus.CLOSED), supervisor))
    }

    @Test
    fun onlyResolversCannotCreateTickets() {
        assertTrue(TicketWorkflow.canCreateTickets(requester))
        assertTrue(TicketWorkflow.canCreateTickets(supervisor))
        assertFalse(TicketWorkflow.canCreateTickets(resolver))
    }
}
