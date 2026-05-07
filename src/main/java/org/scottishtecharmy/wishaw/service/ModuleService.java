package org.scottishtecharmy.wishaw.service;

import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw.entity.*;
import org.scottishtecharmy.wishaw.entity.Module;
import org.scottishtecharmy.wishaw.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;

    @Transactional(readOnly = true)
    public List<Module> findAll() {
        return moduleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Module> findByCentre(Centre centre) {
        return moduleRepository.findByCentre(centre);
    }

    @Transactional(readOnly = true)
    public Module findById(Long id) {
        return moduleRepository.findById(id).orElse(null);
    }

    @Transactional
    public Module save(Module module) {
        return moduleRepository.save(module);
    }

    @Transactional
    public void delete(Long id) {
        moduleRepository.deleteById(id);
    }
}
