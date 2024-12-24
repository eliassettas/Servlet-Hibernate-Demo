package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import factory.DataSourceHolder;
import factory.EntityManagerHolder;
import factory.HibernateHolder;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Message;

@WebServlet("/messages")
public class MessagesServlet extends HttpServlet {

    private static final String SELECT_MESSAGES = "select * from message";
    private static final String INSERT_MESSAGE = "insert into message (description) values (?)";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (EntityManager entityManager = EntityManagerHolder.getEntityManager()) {
            List<Message> messages = entityManager.createQuery("from Message", Message.class)
                    .getResultList();
            resp.getWriter().write(objectMapper.writeValueAsString(messages));
        } catch (IOException e) {
            throw new ServletException("Failure during query execution!");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String body = req.getReader().lines().collect(Collectors.joining("\n"));
        Message message = objectMapper.readValue(body, Message.class);

        String description = message.getDescription();
        if (description == null || description.isBlank()) {
            throw new ServletException("Message is empty or blank!");
        }

        try (EntityManager entityManager = EntityManagerHolder.getEntityManager()) {
            message.setId(null);
            entityManager.getTransaction().begin();
            entityManager.persist(message);
            entityManager.getTransaction().commit();
            resp.getWriter().write("Message posted successfully!");
        } catch (IOException e) {
            throw new RuntimeException("Failure during query execution!");
        }
    }

    //    @Override
    protected void doGetWithHibernate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HibernateHolder.getSessionFactory().inTransaction(session -> {
            List<Message> messages = session.createQuery("from Message", Message.class)
                    .getResultList();
            try {
                resp.getWriter().write(objectMapper.writeValueAsString(messages));
            } catch (IOException e) {
                throw new RuntimeException("Failure during query execution!");
            }
        });
    }

    //    @Override
    protected void doPostWithHibernate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String body = req.getReader().lines().collect(Collectors.joining("\n"));
        Message message = objectMapper.readValue(body, Message.class);

        String description = message.getDescription();
        if (description == null || description.isBlank()) {
            throw new ServletException("Message is empty or blank!");
        }

        HibernateHolder.getSessionFactory().inTransaction(session -> {
            try {
                message.setId(null);
                session.persist(message);
                resp.getWriter().write("Message posted successfully!");
            } catch (IOException e) {
                throw new RuntimeException("Failure during query execution!");
            }
        });
    }

    //    @Override
    protected void doGetWithoutHibernate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (
                Connection connection = DataSourceHolder.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(SELECT_MESSAGES)
        ) {
            ResultSet resultSet = statement.executeQuery();
            List<Message> messages = new LinkedList<>();
            while (resultSet.next()) {
                Message message = new Message();
                message.setId(resultSet.getInt("id"));
                message.setDescription(resultSet.getString("description"));
                messages.add(message);
            }
            resp.getWriter().write(objectMapper.writeValueAsString(messages));
        } catch (SQLException e) {
            throw new ServletException("Failure during query execution!");
        }
    }

    //    @Override
    protected void doPostWithoutHibernate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String body = req.getReader().lines().collect(Collectors.joining("\n"));
        Message message = objectMapper.readValue(body, Message.class);

        String description = message.getDescription();
        if (description == null || description.isBlank()) {
            throw new ServletException("Message is empty or blank!");
        }

        try (
                Connection connection = DataSourceHolder.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT_MESSAGE)
        ) {
            statement.setString(1, description);
            int result = statement.executeUpdate();
            String response = result == 1 ? "Message posted successfully!" : "Failed to post message!";
            resp.getWriter().write(response);
        } catch (SQLException e) {
            throw new ServletException("Failure during query execution!");
        }
    }
}
