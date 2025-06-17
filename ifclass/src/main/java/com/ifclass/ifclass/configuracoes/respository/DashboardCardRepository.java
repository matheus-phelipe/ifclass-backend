package com.ifclass.ifclass.configuracoes.respository;


import com.ifclass.ifclass.configuracoes.model.DashboardCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardCardRepository extends JpaRepository<DashboardCard, Long> {
}
