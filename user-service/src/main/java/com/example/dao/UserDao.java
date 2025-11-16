package com.example.dao;

import com.example.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class UserDao {
    private static final Logger logger = Logger.getLogger(UserDao.class.getName());

    public Long save(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            logger.info("User saved successfully with ID: " + user.getId());
            return user.getId();
        } catch (ConstraintViolationException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.severe("Constraint violation: " + e.getMessage());
            throw new RuntimeException("Email already exists", e);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.severe("Error saving user: " + e.getMessage());
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    public Optional<User> findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.severe("Error finding user by ID: " + e.getMessage());
            throw new RuntimeException("Error finding user", e);
        } finally {
            session.close();
        }
    }

    public List<User> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            logger.severe("Error finding all users: " + e.getMessage());
            throw new RuntimeException("Error finding users", e);
        } finally {
            session.close();
        }
    }

    public void update(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
            logger.info("User updated successfully with ID: " + user.getId());
        } catch (ConstraintViolationException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.severe("Constraint violation: " + e.getMessage());
            throw new RuntimeException("Email already exists", e);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.severe("Error updating user: " + e.getMessage());
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    public void delete(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
                logger.info("User deleted successfully with ID: " + id);
            } else {
                logger.warning("User not found with ID: " + id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.severe("Error deleting user: " + e.getMessage());
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }
}