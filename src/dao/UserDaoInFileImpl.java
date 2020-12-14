package dao;

import model.User;

import java.io.*;
import java.util.*;

public class UserDaoInFileImpl implements UserDao {
    private final List<User> users = new ArrayList<>();
    private final String nameFile;
    private final String regex = ":";

    public UserDaoInFileImpl() {
        nameFile = "src" + File.separator +
                "resources" + File.separator +
                "userDB.txt";
        loadUsersFromFile(nameFile);
    }

    @Override
    public void saveUser(User user) {
        if(getUserByName(user.getUserName()) == null) {
            users.add(user);
            writeUserToFile(user);
        }
    }

    @Override
    public void deleteUser(User user) {
        updateUserFromFile(user,"");
        users.remove(user);
    }

    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public User getUserByName(String userName) {
        return users.stream()
                .filter(user -> user.getUserName()
                        .equals(userName)).findFirst().orElse(null);
    }

    @Override
    public void updateUser(User oldUser, User newUser) {
        int userIndex = users.indexOf(oldUser);
        if(userIndex >= 0) {
            String stringNewUser = newUser.getUserName() + regex +
                    newUser.getPassword() + System.lineSeparator();
            updateUserFromFile(oldUser, stringNewUser);
            users.set(userIndex, newUser);
        }
    }

    private void addUserFromLine(String line) {
        String[] array = line.split(regex);
        if(array.length == 2) {
            users.add(new User(array[0], array[1]));
        }
    }

    private void loadUsersFromFile(String nameFile) {
        try (
             FileReader fileReader = new FileReader(nameFile);
             BufferedReader reader = new BufferedReader(fileReader)
        ) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                addUserFromLine(currentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeUserToFile(User user) {
        try (
             PrintWriter out = new PrintWriter(new FileWriter(nameFile, true))
        ) {
            out.println(user.getUserName() + regex + user.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateUserFromFile(User user, String update) {
        Scanner sc = null;
        try {
            sc = new Scanner(new File(nameFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String fileContents;
        StringBuilder buffer = new StringBuilder();
        FileWriter writer;
        if(sc != null) {
            while (sc.hasNextLine()) {
                buffer.append(sc.nextLine()).append(System.lineSeparator());
            }
            fileContents = buffer.toString();
            fileContents = fileContents.replaceAll(user.getUserName() + regex +
                    user.getPassword() + System.lineSeparator(), update);
            sc.close();
            try {
                writer = new FileWriter(nameFile);
                writer.append(fileContents);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
