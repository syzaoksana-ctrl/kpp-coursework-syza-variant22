package ua.edu.nupp.kpp.server;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
public class WorkServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(WorkServlet.class.getName());
    private final WorkService service = new WorkService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if ("add".equals(action)) {
                service.add(readWork(req, null));
            } else if ("update".equals(action)) {
                service.update(readWork(req, id(req)));
            } else if ("delete".equals(action)) {
                service.delete(id(req));
            } else {
                throw new IllegalArgumentException("Невідома операція");
            }
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private Work readWork(HttpServletRequest req, Long id) {
        return new Work(id,
                Long.parseLong(param(req, "orderId")),
                req.getParameter("name"),
                Double.parseDouble(param(req, "price")),
                Double.parseDouble(param(req, "hours")));
    }

    private String param(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Не вказано параметр: " + name);
        }
        return value.trim();
    }

    private long id(HttpServletRequest req) {
        return Long.parseLong(param(req, "id"));
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp, Exception e) throws IOException {
        if (e instanceof IllegalArgumentException) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, Map.of("error", e.getMessage() == null ? "Некоректні дані" : e.getMessage()));
        } else {
            LOG.log(Level.SEVERE, "Помилка сервера", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, Map.of("error", "Внутрішня помилка сервера"));
        }
    }
}
