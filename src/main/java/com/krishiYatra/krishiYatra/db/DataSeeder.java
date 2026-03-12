package com.krishiYatra.krishiYatra.db;

import com.krishiYatra.krishiYatra.common.enums.RoleType;
import com.krishiYatra.krishiYatra.user.RoleEntity;
import com.krishiYatra.krishiYatra.user.RoleRepo;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.user.UserRepo;
import com.krishiYatra.krishiYatra.stock.category.CategoryRepo;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepo categoryRepo;
    private final SubCategoryRepo subCategoryRepo;

    @Override
    public void run(String... args) {
        String branch = getCurrentGitBranch();
        if ("main".equals(branch)) {
            log.info("Running on 'main' branch. Skipping data seeding to protect production database.");
            return;
        }

        log.info("Running on branch '{}' (H2 Environment). Starting data seeding...", branch);
        seedRoles();
        seedAdminUser();
        seedNormalUser();
        verifyStockSeeding();
    }

    private String getCurrentGitBranch() {
        try {
            java.nio.file.Path dotGitHead = java.nio.file.Paths.get(".git/HEAD");
            if (!java.nio.file.Files.exists(dotGitHead)) {
                return "unknown";
            }
            String head = java.nio.file.Files.readString(dotGitHead).trim();
            if (head.startsWith("ref:")) {
                String[] parts = head.split("/");
                return parts[parts.length - 1];
            }
        } catch (java.io.IOException e) {
            log.error("Failed to detect git branch for data seeding", e);
        }
        return "unknown";
    }

    private void seedRoles() {
        for (RoleType type : RoleType.values()) {
            if (roleRepo.findByRoleName(type).isEmpty()) {
                RoleEntity role = new RoleEntity();
                role.setRoleName(type);
                roleRepo.save(role);
                log.info("Seeded role: {}", type);
            }
        }
    }

    private void seedAdminUser() {
        String adminUsername = "admin";
        if (userRepo.findByUsername(adminUsername).isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setFullName("System Administrator");
            admin.setUsername(adminUsername);
            admin.setEmail("admin@krishiyatra.com");
            admin.setPhoneNumber("9800000000");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setActive(true);

            RoleEntity adminRole = roleRepo.findByRoleName(RoleType.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            
            admin.setRoles(Set.of(adminRole));
            
            userRepo.save(admin);
            log.info("Seeded default admin user: {} / admin123", adminUsername);
        }
    }

    private void seedNormalUser() {
        String username = "user";
        if (userRepo.findByUsername(username).isPresent()) {
            UserEntity user = userRepo.findByUsername(username).get();
            user.setPassword(passwordEncoder.encode("user123"));
            userRepo.save(user);
            log.info("Normal user '{}' verified and password updated via DataSeeder.", username);
            return;
        }
        log.warn("Normal user '{}' not found — expected from stub migration.", username);
    }

    private void verifyStockSeeding() {
        long catCount = categoryRepo.count();
        long subCount = subCategoryRepo.count();
        log.info("Stock System Status: {} Categories and {} Sub-categories are active (loaded via Stub Migrations).", catCount, subCount);
    }
}
