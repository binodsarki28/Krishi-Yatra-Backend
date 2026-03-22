package com.krishiYatra.krishiYatra.db;

import com.krishiYatra.krishiYatra.address.AddressEntity;
import com.krishiYatra.krishiYatra.address.AddressRepo;
import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.buyer.BuyerRepo;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.farmer.FarmerRepo;
import com.krishiYatra.krishiYatra.common.enums.ConsumerType;
import com.krishiYatra.krishiYatra.common.enums.RoleType;
import com.krishiYatra.krishiYatra.common.enums.VehicleType;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import com.krishiYatra.krishiYatra.delivery.DeliveryEntity;
import com.krishiYatra.krishiYatra.delivery.DeliveryRepo;
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
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final BuyerRepo buyerRepo;
    private final DeliveryRepo deliveryRepo;
    private final FarmerRepo farmerRepo;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepo categoryRepo;
    private final SubCategoryRepo subCategoryRepo;
    private final AddressRepo addressRepo;

    @Override
    public void run(String... args) {
        String branch = getCurrentGitBranch();
        if ("main".equals(branch)) {
            log.info("Running on 'main' branch. Skipping data seeding.");
            return;
        }

        log.info("Data Seeding started for branch '{}'...", branch);
        seedRoles();
        seedAdminUser();
        seedNormalUser();
        seedTestUsers();
        verifyStockSeeding();
    }

    private String getCurrentGitBranch() {
        try {
            java.nio.file.Path dotGitHead = java.nio.file.Paths.get(".git/HEAD");
            if (!java.nio.file.Files.exists(dotGitHead)) return "unknown";
            String head = java.nio.file.Files.readString(dotGitHead).trim();
            if (head.startsWith("ref:")) {
                String[] parts = head.split("/");
                return parts[parts.length - 1];
            }
        } catch (Exception e) {}
        return "unknown";
    }

    private void seedRoles() {
        for (RoleType type : RoleType.values()) {
            if (roleRepo.findByRoleName(type).isEmpty()) {
                RoleEntity role = new RoleEntity();
                role.setRoleName(type);
                roleRepo.save(role);
            }
        }
    }

    private void seedAdminUser() {
        String username = "admin";
        if (userRepo.findByUsername(username).isEmpty()) {
            UserEntity user = new UserEntity();
            user.setFullName("System Administrator");
            user.setUsername(username);
            user.setEmail("admin@test.com");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setActive(true);
            user.setRoles(Set.of(roleRepo.findByRoleName(RoleType.ADMIN).get()));
            userRepo.save(user);
        }
    }

    private void seedNormalUser() {
        String username = "user";
        if (userRepo.findByUsername(username).isPresent()) {
            UserEntity user = userRepo.findByUsername(username).get();
            user.setPassword(passwordEncoder.encode("user123"));
            userRepo.save(user);
        }
    }

    private void seedTestUsers() {
        createTestUserIfMissing("buyer", "buyer123", RoleType.BUYER, "Verified Buyer User");
        createTestUserIfMissing("delivery", "delivery123", RoleType.DELIVERY, "Verified Delivery User");
        
        // Ensure Buyer Entity is VERIFIED
        userRepo.findByUsername("buyer").ifPresent(user -> {
            if (buyerRepo.findByUser(user).isEmpty()) {
                BuyerEntity buyer = new BuyerEntity();
                buyer.setUser(user);
                buyer.setBusinessName("Buyer Test Shop");
                buyer.setBusinessLocation("Kathmandu");
                buyer.setConsumerType(ConsumerType.RETAILER);
                buyer.setStatus(VerificationStatus.VERIFIED);
                buyerRepo.save(buyer);
                log.info("Seeded and VERIFIED buyer entity for 'buyer'");
            } else {
                BuyerEntity buyer = buyerRepo.findByUser(user).get();
                buyer.setStatus(VerificationStatus.VERIFIED);
                buyerRepo.save(buyer);
            }

            // Seed Address for Buyer
            if (addressRepo.findByUser(user).isEmpty()) {
                AddressEntity address = new AddressEntity();
                address.setUser(user);
                address.setProvince("Bagmati Province");
                address.setDistrict("Kathmandu");
                address.setMunicipality("Kathmandu Metropolitan City");
                address.setWardNo("1");
                address.setOther("Baneshwor");
                addressRepo.save(address);
            }
        });

        // Ensure Delivery Entity is VERIFIED
        userRepo.findByUsername("delivery").ifPresent(user -> {
            if (deliveryRepo.findByUser(user).isEmpty()) {
                DeliveryEntity delivery = new DeliveryEntity();
                delivery.setUser(user);
                delivery.setVehicleType(VehicleType.MOTORCYCLE);
                delivery.setVehicleBrand("Honda");
                delivery.setNumberPlate("BA 1 PA 1234");
                delivery.setLicenseNumber("11-22-33333333");
                delivery.setStatus(VerificationStatus.VERIFIED);
                deliveryRepo.save(delivery);
                log.info("Seeded and VERIFIED delivery entity for 'delivery'");
            } else {
                DeliveryEntity delivery = deliveryRepo.findByUser(user).get();
                delivery.setStatus(VerificationStatus.VERIFIED);
                deliveryRepo.save(delivery);
            }
        });

        // Ensure Farmer Entity (testuser) is VERIFIED
        createTestUserIfMissing("testuser", "testuser123", RoleType.FARMER, "Verified Farmer User");
        userRepo.findByUsername("testuser").ifPresent(user -> {
            if (farmerRepo.findByUser(user).isEmpty()) {
                FarmerEntity farmer = new FarmerEntity();
                farmer.setUser(user);
                farmer.setFarmName("Test Farm");
                farmer.setFarmLocation("Chitwan");
                farmer.setFarmArea(5.5);
                farmer.setStatus(VerificationStatus.VERIFIED);
                farmerRepo.save(farmer);
                log.info("Seeded and VERIFIED farmer entity for 'testuser'");
            } else {
                FarmerEntity farmer = farmerRepo.findByUser(user).get();
                farmer.setStatus(VerificationStatus.VERIFIED);
                farmerRepo.save(farmer);
            }

            // Seed Address for Farmer (testuser)
            if (addressRepo.findByUser(user).isEmpty()) {
                AddressEntity address = new AddressEntity();
                address.setUser(user);
                address.setProvince("Bagmati Province");
                address.setDistrict("Chitwan");
                address.setMunicipality("Bharatpur Metropolitan City");
                address.setWardNo("10");
                address.setOther("Madi-10");
                addressRepo.save(address);
            }
        });
    }

    private void createTestUserIfMissing(String username, String password, RoleType roleType, String fullName) {
        if (userRepo.findByUsername(username).isEmpty()) {
            UserEntity user = new UserEntity();
            user.setFullName(fullName);
            user.setUsername(username);
            user.setEmail(username + "@test.com");
            user.setPhoneNumber("98" + (int)(Math.random() * 90000000 + 10000000));
            user.setPassword(passwordEncoder.encode(password));
            user.setActive(true);
            user.setRoles(Set.of(roleRepo.findByRoleName(roleType).get()));
            userRepo.save(user);
            log.info("Seeded test user: {} / {}", username, password);
        } else {
            UserEntity user = userRepo.findByUsername(username).get();
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(Set.of(roleRepo.findByRoleName(roleType).get()));
            userRepo.save(user);
        }
    }

    private void verifyStockSeeding() {
        log.info("Stock verification check: {} Categories, {} Sub-categories active.", categoryRepo.count(), subCategoryRepo.count());
    }
}
