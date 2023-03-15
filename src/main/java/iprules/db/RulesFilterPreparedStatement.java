package iprules.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@RequiredArgsConstructor
public class RulesFilterPreparedStatement {
    public static final String ALLOW = "allow";
    private final DataSource dataSource;

    @Value("classpath:db/ip_filter_match.sql")
    private Resource resource;

    public Optional<Boolean> isIpAllowed(String sourceIp, String destinationIp) throws SQLException {
        String query = getQuery();
        String sql = query.formatted(sourceIp, sourceIp, sourceIp,
                destinationIp, destinationIp, destinationIp);

        PreparedStatement statement = dataSource.getConnection().prepareStatement(sql);

        ResultSet rs = statement.executeQuery();
        Optional<Boolean> allow = Optional.empty();
        if (rs.next()) {
            allow = Optional.of(rs.getBoolean(ALLOW));
        }
        rs.close();
        statement.close();
        return allow;
    }

    public String getQuery() {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
