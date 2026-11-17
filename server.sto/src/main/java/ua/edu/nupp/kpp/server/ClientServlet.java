package ua.edu.nupp.kpp.server;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
public class ClientServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(ClientServlet.class.getName());
    private final ClientService service = new ClientService();
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
                service.add(readClient(req, null));
            } else if ("update".equals(action)) {
                service.update(readClient(req, id(req)));
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

    private Client readClient(HttpServletRequest req, Long id) {
        return new Client(id, req.getParameter("fullName"),
                req.getParameter("phone"), req.getParameter("email"));
    }

    private long id(HttpServletRequest req) {
        String value = req.getParameter("id");
        if (value == null) throw new IllegalArgumentException("Не вказано id");
        return Long.parseLong(value.trim());
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