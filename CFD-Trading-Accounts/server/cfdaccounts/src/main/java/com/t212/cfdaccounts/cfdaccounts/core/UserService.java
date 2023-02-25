//package com.t212.cfdaccounts.cfdaccounts.core;
//
//import com.t212.cfdaccounts.cfdaccounts.core.models.User;
//import com.t212.cfdaccounts.cfdaccounts.repositories.UserRepository;
//import com.t212.cfdaccounts.cfdaccounts.repositories.models.UserDAO;
//import org.springframework.dao.DataAccessException;
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class UserService {
//    private final UserRepository userRepository;
//
//    public UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public User getById(long userId) throws EmptyResultDataAccessException {
//        UserDAO user = userRepository.getUser(userId);
//        return Mappers.fromResultSetToUser(user);
//    }
//
//    public List<User> listUsers(Integer page, Integer pageSize) {
//        return userRepository.listUsers(page, pageSize).stream().map(current -> Mappers.fromResultSetToUser(current)).collect(Collectors.toList());
//    }
//
//    public boolean removeUser(long userId) throws DataAccessException {
//        return userRepository.deleteUser(userId);
//    }
//
//}
