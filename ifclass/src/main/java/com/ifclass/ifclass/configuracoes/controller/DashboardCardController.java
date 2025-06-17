package com.ifclass.ifclass.configuracoes.controller;

import com.ifclass.ifclass.configuracoes.model.DashboardCard;
import com.ifclass.ifclass.configuracoes.service.DashboardCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configuracoes/dashboard-cards") // Endpoint para DashboardCards
@CrossOrigin(origins = "*") // Permite chamadas de qualquer origem
public class DashboardCardController {
    @Autowired
    private DashboardCardService service;

    @GetMapping
    public ResponseEntity<List<DashboardCard>> getAllCards() {
        return ResponseEntity.ok(service.getAllCards());
    }

    @GetMapping("/enabled") // Endpoint para cards habilitados e ordenados (para o frontend)
    public ResponseEntity<List<DashboardCard>> getEnabledCardsOrdered() {
        return ResponseEntity.ok(service.getEnabledCardsOrdered());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DashboardCard> getCardById(@PathVariable Long id) {
        return service.getCardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DashboardCard> createCard(@RequestBody DashboardCard card) {
        DashboardCard savedCard = service.saveCard(card);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCard);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DashboardCard> updateCard(@PathVariable Long id, @RequestBody DashboardCard card) {
        DashboardCard updatedCard = service.updateCard(id, card);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long id) {
        service.deleteCard(id);
    }

    @PutMapping("/reorder") // Endpoint para atualizar a ordem dos cards (via drag-drop)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderCards(@RequestBody List<Long> cardIdsInNewOrder) {
        service.updateCardOrder(cardIdsInNewOrder);
    }
}
