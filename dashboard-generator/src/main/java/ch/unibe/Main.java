package ch.unibe;


import com.grafana.foundation.common.Constants;
import com.grafana.foundation.common.GraphGradientMode;
import com.grafana.foundation.common.LegendDisplayMode;
import com.grafana.foundation.common.LegendPlacement;
import com.grafana.foundation.common.LineInterpolation;
import com.grafana.foundation.common.ReduceDataOptionsBuilder;
import com.grafana.foundation.common.VizLegendOptionsBuilder;
import com.grafana.foundation.dashboard.Dashboard;
import com.grafana.foundation.dashboard.DashboardBuilder;
import com.grafana.foundation.dashboard.DashboardDashboardTimeBuilder;
import com.grafana.foundation.dashboard.DataSourceRef;
import com.grafana.foundation.dashboard.FieldColorBuilder;
import com.grafana.foundation.dashboard.FieldColorModeId;
import com.grafana.foundation.dashboard.RowBuilder;
import com.grafana.foundation.dashboard.Threshold;
import com.grafana.foundation.dashboard.ThresholdsConfigBuilder;
import com.grafana.foundation.dashboard.ThresholdsMode;
import com.grafana.foundation.gauge.GaugePanelBuilder;
import com.grafana.foundation.prometheus.DataqueryBuilder;
import com.grafana.foundation.timeseries.TimeseriesPanelBuilder;
import com.grafana.relocated.jackson.core.JsonProcessingException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main class for generating a Grafana dashboard using the Grafana Java SDK.
 * <p>
 * This class demonstrates how to programmatically build a dashboard containing
 * time series and gauge panels for CPU workload monitoring. The generated dashboard
 * is saved as a JSON file.
 */
public class Main {
    /**
     * Creates a time series panel for monitoring CPU usage.
     *
     * @return a configured {@link TimeseriesPanelBuilder} instance
     */
    public static void main(String[] args) throws JsonProcessingException {
        Dashboard dashboard = new DashboardBuilder("MyDashboard").
                uid("generated-from-java").
                tags(List.of("generated", "from", "java")).
                refresh("5s").
                time(new DashboardDashboardTimeBuilder().
                        from("now-5m").
                        to("now")
                ).
                timezone(Constants.TimeZoneBrowser).
                withRow(new RowBuilder("Task #2")).
                withPanel(getTimeSeriesCpuPanel())
                .withPanel(getGaugeCpuPanel())
                .build();

        System.out.println(dashboard.toJSON());

        Path path = Paths.get("generated-dashboard", "dashboard.json");
        Utils.storeToFile(path, dashboard.toJSON());
    }

    /**
     * Creates a time series panel for monitoring CPU usage.
     *
     * @return a configured {@link TimeseriesPanelBuilder} instance
     */
    public static TimeseriesPanelBuilder getTimeSeriesCpuPanel() {
        return new TimeseriesPanelBuilder().
                title("CPU timeseries workload").
                description("This panel provides information about CPU workload.").
                unit("percent").
                min(0.0).
                max(100.0).
                colorScheme(new FieldColorBuilder().mode(FieldColorModeId.THRESHOLDS)).
                gradientMode(GraphGradientMode.SCHEME).
                lineInterpolation(LineInterpolation.SMOOTH).
                fillOpacity(7.0).
                thresholds(getThresholdsBuilder()).
                withTarget(new DataqueryBuilder().
                        datasource(new DataSourceRef("prometheus", "DS_PROMETHEUS_UID")).
                        expr("avg by(instance) (cpu_usage{instance=\"server1\"})").
                        legendFormat("__auto")
                ).legend(new VizLegendOptionsBuilder()
                        .showLegend(true)
                        .placement(LegendPlacement.BOTTOM)
                        .displayMode(LegendDisplayMode.LIST)
                        .calcs(List.of("mean")));
    }

    /**
     * Creates a gauge panel for monitoring CPU usage.
     *
     * @return a configured {@link GaugePanelBuilder} instance
     */
    public static GaugePanelBuilder getGaugeCpuPanel() {
        return new GaugePanelBuilder().
                title("CPU Gauge").
                description("This panel provides information gauge CPU workload.").
                unit("percent").
                min(0.0).
                max(100.0).
                thresholds(getThresholdsBuilder()).
                withTarget(new DataqueryBuilder().
                        datasource(new DataSourceRef("prometheus", "DS_PROMETHEUS_UID")).
                        expr("cpu_usage{instance=\"server1\"}").
                        legendFormat("__auto")
                ).reduceOptions(new ReduceDataOptionsBuilder()
                        .calcs(List.of("lastNotNull"))
                        .values(false));
    }

    /**
     * Creates a threshold builder
     *
     * @return a configured {@link ThresholdsConfigBuilder} instance
     */
    public static ThresholdsConfigBuilder getThresholdsBuilder() {
        return new ThresholdsConfigBuilder()
                .mode(ThresholdsMode.PERCENTAGE)
                .steps(List.of(
                        new Threshold(0.0, "green"),
                        new Threshold(60.0, "yellow"),
                        new Threshold(80.0, "red")));
    }
}