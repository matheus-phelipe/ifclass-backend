package com.ifclass.ifclass.configuracoes.respository;

import com.ifclass.ifclass.configuracoes.model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, String> {
}