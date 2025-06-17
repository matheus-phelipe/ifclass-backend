package com.ifclass.ifclass.configuracoes.service;

import com.ifclass.ifclass.common.exception.ResourceNotFoundException;
import com.ifclass.ifclass.configuracoes.model.DashboardCard;
import com.ifclass.ifclass.configuracoes.respository.DashboardCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator; // Para ordenação
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Para Java 8+ streams

@Service
public class DashboardCardService {
    @Autowired
    private DashboardCardRepository repository;

    public List<DashboardCard> getAllCards() {
        // Ordena os cards pela propriedade 'cardOrder'
        return repository.findAll().stream()
                .sorted(Comparator.comparingInt(DashboardCard::getCardOrder))
                .collect(Collectors.toList());
    }

    public List<DashboardCard> getEnabledCardsOrdered() {
        return repository.findAll().stream()
                .filter(DashboardCard::isEnabled) // Filtra apenas cards habilitados
                .sorted(Comparator.comparingInt(DashboardCard::getCardOrder))
                .collect(Collectors.toList());
    }

    public Optional<DashboardCard> getCardById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public DashboardCard saveCard(DashboardCard card) {
        // Ao criar um novo card, defina a ordem como o próximo número disponível
        if (card.getId() == null) {
            int nextOrder = repository.findAll().stream()
                    .mapToInt(DashboardCard::getCardOrder)
                    .max().orElse(0) + 1;
            card.setCardOrder(nextOrder);
        }
        // Validações básicas (pode expandir)
        if (card.getTitle() == null || card.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("O título do card não pode ser vazio.");
        }
        return repository.save(card);
    }

    @Transactional
    public DashboardCard updateCard(Long id, DashboardCard updatedCard) {
        return repository.findById(id)
                .map(existingCard -> {
                    existingCard.setIcon(updatedCard.getIcon());
                    existingCard.setTitle(updatedCard.getTitle());
                    existingCard.setText(updatedCard.getText());
                    existingCard.setLink(updatedCard.getLink());
                    existingCard.setRoles(updatedCard.getRoles());
                    existingCard.setEnabled(updatedCard.isEnabled());
                    existingCard.setCardOrder(updatedCard.getCardOrder()); // Ordem pode ser alterada via drag-drop
                    return repository.save(existingCard);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Card não encontrado com ID: " + id));
    }

    @Transactional
    public void deleteCard(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Card não encontrado com ID: " + id + " para exclusão.");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void updateCardOrder(List<Long> cardIdsInNewOrder) {
        List<DashboardCard> allCards = repository.findAll();
        for (int i = 0; i < cardIdsInNewOrder.size(); i++) {
            Long cardId = cardIdsInNewOrder.get(i);
            int newOrder = i;
            allCards.stream()
                    .filter(card -> card.getId().equals(cardId))
                    .findFirst()
                    .ifPresent(card -> card.setCardOrder(newOrder));
        }
        repository.saveAll(allCards); // Salva todos os cards com as novas ordens
    }
}