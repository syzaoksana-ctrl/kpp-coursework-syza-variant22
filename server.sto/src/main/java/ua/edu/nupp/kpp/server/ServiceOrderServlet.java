package ua.edu.nupp.kpp.server;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
public class ServiceOrderServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(ServiceOrderServlet.class.getName());
    private final ServiceOrderService service = new ServiceOrderService();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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
                service.add(readOrder(req, null));
            } else if ("update".equals(action)) {
                service.update(readOrder(req, id(req)));
            } else if ("delete".equals(action)) {
                service.delete(id(req));
            } else if ("transition".equals(action)) {
                service.changeState(id(req), OrderState.valueOf(param(req, "state")));
            } else {
                throw new IllegalArgumentException("Невідома операція");
            }
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private ServiceOrder readOrder(HttpServletRequest req, Long id) {
        ServiceOrder order = new ServiceOrder();
        order.setId(id);
        order.setCarId(Long.parseLong(param(req, "carId")));
        order.setDescription(req.getParameter("description"));
        order.setF1(Double.parseDouble(param(req, "f1")));
        order.setF2(Double.parseDouble(param(req, "f2")));
        return order;
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
