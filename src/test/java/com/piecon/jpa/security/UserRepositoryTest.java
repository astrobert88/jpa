package com.piecon.jpa.security;

import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
// Uncomment if want to test against embedded (H2) database
//@DataJpaTest
@SpringBootTest
@Slf4j
@AutoConfigureTestDatabase
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeClass
    public static void setup() {
        log.info("setup()");
    }

    @AfterClass
    public static void tearDown() {
        log.info("tearDown()");
    }

    @Before
    public void init() {
        log.info("init()");
    }

    @After
    public void finalise() {
        // After each test, delete entries in user and role tables
        log.info("finalise()");
        userRepository.deleteAll();
        userRepository.flush();
        roleRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    public void testSaveUsersThenFindAll() {
        log.info("testSaveUsersThenFindAll()");

        // Before any users are stored, should return empty collection
        List<User> users = userRepository.findAll();
        Assert.assertNotNull(users);
        Assert.assertEquals(0, users.size());

        // Create and save a user
        User santa = new User("Santa", "merryXmas", true, true, true, true);
        userRepository.save(santa);

        // Now should return a single user, santa
        users = userRepository.findAll();
        Assert.assertNotNull(users);
        Assert.assertEquals(1, users.size());

        User user = users.get(0);
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());
        Assert.assertEquals("Santa", user.getUsername());
        Assert.assertEquals("merryXmas", user.getPassword());
        Assert.assertTrue(user.isEnabled());
        Assert.assertTrue(user.isAccountNonExpired());
        Assert.assertTrue(user.isAccountNonLocked());
        Assert.assertTrue(user.isCredentialsNonExpired());

        Collection<Role> authorities = user.getAuthorities();
        Assert.assertNotNull(authorities);
        Assert.assertEquals(0, authorities.size());

        // Create and save another user
        User rudolph = new User("Rudolph", "sniff", true, true, true, true);
        userRepository.save(rudolph);

        // Now should return two users, santa and rudolph
        users = userRepository.findAll();
        Assert.assertNotNull(users);
        Assert.assertEquals(2, users.size());

        user = users.get(1);
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());
        Assert.assertEquals("Rudolph", user.getUsername());
        Assert.assertEquals("sniff", user.getPassword());
        Assert.assertTrue(user.isEnabled());
        Assert.assertTrue(user.isAccountNonExpired());
        Assert.assertTrue(user.isAccountNonLocked());
        Assert.assertTrue(user.isCredentialsNonExpired());

        authorities = user.getAuthorities();
        Assert.assertNotNull(authorities);
        Assert.assertEquals(0, authorities.size());
    }

    @Test
    public void testSaveTwoUsersWithSameUsernameFails() {
        log.info("testSaveTwoUsersWithSameUsernameFails()");

        // Save a user with username Root
        User root = new User("Root", "rootpwd", true, true, true, true);
        userRepository.saveAndFlush(root);

        // Try and save another user with username Root
        User root2 = new User("Root", "rootpwd", true, true, true, true);
        Assert.assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(root2));
    }

    @Test
    public void testSaveUserWithAuthorities() {
        log.info("testSaveUserWithAuthorities()");

        // Create and save a role and a user
        Role ROLE_USER = new Role("ROLE_USER");
        roleRepository.saveAndFlush(ROLE_USER);

        User user = new User("User", "password", true, true, true, true);
        user.setAuthorities(Collections.singletonList(ROLE_USER));
        userRepository.saveAndFlush(user);

        List<User> users = userRepository.findAll();
        Assert.assertNotNull(users);
        Assert.assertEquals(1, users.size());
        user = users.get(0);
        Assert.assertNotNull(user);
        Assert.assertEquals("User", user.getUsername());

        List<Role> userRoles = (List<Role>) user.getAuthorities();
        Assert.assertNotNull(userRoles );
        Assert.assertEquals(1, userRoles.size());
        Role role = userRoles .get(0);
        Assert.assertNotNull(role);
        Assert.assertEquals("ROLE_USER", role.getAuthority());

        List<Role> roles = roleRepository.findAll();
        Assert.assertNotNull(roles);
        Assert.assertEquals(1, roles.size());
        role = roles.get(0);
        Assert.assertNotNull(role);
        Assert.assertEquals("ROLE_USER", role.getAuthority());
    }

    @Test
    public void testSaveUsersWithAuthorities() {
        log.info("testSaveUsersWithAuthorities()");

        // Create and save roles
        Role ROLE_ROOT = new Role("ROLE_ROOT");
        roleRepository.save(ROLE_ROOT);
        Role ROLE_ADMIN = new Role("ROLE_ADMIN");
        roleRepository.save(ROLE_ADMIN);
        Role ROLE_USER = new Role("ROLE_USER");
        roleRepository.saveAndFlush(ROLE_USER);

        // Sanity check
        List<Role> roles = roleRepository.findAll();
        Assert.assertNotNull(roles);
        Assert.assertEquals(3, roles.size());
        Assert.assertTrue(roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ROOT")));
        Assert.assertTrue(roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")));
        Assert.assertTrue(roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER")));


        // Create and save users
        User root = new User("Root", "rootpwd", true, true, true, true);
        root.setAuthorities(Arrays.asList(ROLE_ROOT, ROLE_ADMIN, ROLE_USER));
        userRepository.save(root);
        User admin = new User("Admin", "adminpwd", true, true, true, true);
        admin.setAuthorities(Arrays.asList(ROLE_ADMIN, ROLE_USER));
        userRepository.save(admin);
        User user = new User("User", "userpwd", true, true, true, true);
        user.setAuthorities(Collections.singletonList(ROLE_USER));
        userRepository.saveAndFlush(user);

        // Check them
        root = userRepository.findByUsername("Root");
        Assert.assertNotNull(root);
        Assert.assertEquals("Root", root.getUsername());
        roles = (List<Role>) root.getAuthorities();
        Assert.assertNotNull(roles);
        Assert.assertEquals(3, roles.size());
        Assert.assertTrue(roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ROOT")));
        Assert.assertTrue(roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")));
        Assert.assertTrue(roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER")));

        admin = userRepository.findByUsername("Admin");
        Assert.assertNotNull(admin);
        Assert.assertEquals("Admin", admin.getUsername());
        roles = (List<Role>) admin.getAuthorities();
        Assert.assertNotNull(roles);
        Assert.assertEquals(2, roles.size());
        Assert.assertTrue(roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")));
        Assert.assertTrue(roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER")));

        user = userRepository.findByUsername("User");
        Assert.assertNotNull(user);
        Assert.assertEquals("User", user.getUsername());
        roles = (List<Role>) user.getAuthorities();
        Assert.assertNotNull(roles);
        Assert.assertEquals(1, roles.size());
        Assert.assertTrue(roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER")));
    }
}
