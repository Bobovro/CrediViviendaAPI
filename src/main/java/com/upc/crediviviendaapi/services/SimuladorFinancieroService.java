package com.upc.crediviviendaapi.services;

import com.upc.crediviviendaapi.dtos.CuotaDTO;
import com.upc.crediviviendaapi.dtos.PrestamoDTO;
import com.upc.crediviviendaapi.dtos.SimulacionRequestDTO;
import com.upc.crediviviendaapi.dtos.SimulacionResponseDTO;
import com.upc.crediviviendaapi.interfaces.ISimuladorFinancieroService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimuladorFinancieroService implements ISimuladorFinancieroService {

    @Override
    public SimulacionResponseDTO simular(SimulacionRequestDTO request) {

        PrestamoDTO dto = new PrestamoDTO();
        dto.setMontoPrestamo(request.getMontoPrestamo());
        dto.setPlazoMeses(request.getPlazoMeses());
        dto.setTipoTasa(request.getTipoTasa());
        dto.setCapitalizacion(request.getCapitalizacion());
        dto.setTasaInteres(request.getTasaInteres());
        dto.setGraciaTotal(request.getGraciaTotal());
        dto.setGraciaParcial(request.getGraciaParcial());

        PrestamoDTO calculado = calcularPrestamo(dto);

        SimulacionResponseDTO response = new SimulacionResponseDTO();

        // "cuota" para mostrar: primera cuota distinta de gracia total
        response.setCuota(
                calculado.getCronograma().stream()
                        .filter(c -> (c.getEsGraciaTotal() == null || !c.getEsGraciaTotal()))
                        .filter(c -> (c.getEsGraciaParcial() == null || !c.getEsGraciaParcial()))
                        .findFirst()
                        .map(CuotaDTO::getCuotaTotal)
                        .orElse(0.0)
        );

        response.setVan(calculado.getVan());
        response.setTir(calculado.getTir());
        response.setCronograma(calculado.getCronograma());

        return response;
    }

    @Override
    public PrestamoDTO calcularPrestamo(PrestamoDTO prestamoDTO) {

        // -------- Validaciones mínimas ----------
        if (prestamoDTO.getMontoPrestamo() == null || prestamoDTO.getMontoPrestamo() <= 0) {
            throw new IllegalArgumentException("Monto del préstamo inválido");
        }
        if (prestamoDTO.getPlazoMeses() == null || prestamoDTO.getPlazoMeses() <= 0) {
            throw new IllegalArgumentException("Plazo de meses inválido");
        }
        if (prestamoDTO.getTasaInteres() == null || prestamoDTO.getTasaInteres() <= 0) {
            throw new IllegalArgumentException("Tasa de interés inválida");
        }
        if (prestamoDTO.getTipoTasa() == null || prestamoDTO.getTipoTasa().isBlank()) {
            throw new IllegalArgumentException("Tipo de tasa es obligatorio (EFECTIVA / NOMINAL)");
        }

        double monto = prestamoDTO.getMontoPrestamo();
        int n = prestamoDTO.getPlazoMeses();

        int graciaTotal = prestamoDTO.getGraciaTotal() != null ? prestamoDTO.getGraciaTotal() : 0;
        int graciaParcial = prestamoDTO.getGraciaParcial() != null ? prestamoDTO.getGraciaParcial() : 0;

        if (graciaTotal < 0 || graciaParcial < 0) {
            throw new IllegalArgumentException("Las gracias no pueden ser negativas");
        }
        if (graciaTotal + graciaParcial >= n) {
            throw new IllegalArgumentException(
                    "La suma de gracia total + parcial debe ser MENOR al plazo, para que existan cuotas francesas."
            );
        }

        double tasaMensual = convertirATasaMensual(
                prestamoDTO.getTasaInteres(),
                prestamoDTO.getTipoTasa(),
                prestamoDTO.getCapitalizacion()
        );

        if (tasaMensual <= 0) {
            throw new IllegalArgumentException("La tasa mensual resultante es inválida");
        }

        List<CuotaDTO> cronograma = new ArrayList<>();
        double saldo = monto;

        // -------- GRACIA TOTAL ----------
        // cuotaTotal = 0, el interés se capitaliza (saldo sube)
        for (int i = 1; i <= graciaTotal; i++) {
            double interes = saldo * tasaMensual;
            double saldoFinal = saldo + interes;

            CuotaDTO cuota = new CuotaDTO();
            cuota.setNumeroCuota(i);
            cuota.setSaldoInicial(r2(saldo));
            cuota.setInteres(r2(interes));
            cuota.setAmortizacion(0.0);
            cuota.setCuotaTotal(0.0);
            cuota.setSaldoFinal(r2(saldoFinal));
            cuota.setEsGraciaTotal(true);
            cuota.setEsGraciaParcial(false);

            cronograma.add(cuota);
            saldo = saldoFinal;
        }

        // -------- GRACIA PARCIAL ----------
        // cuotaTotal = interés, saldo se mantiene
        for (int i = graciaTotal + 1; i <= graciaTotal + graciaParcial; i++) {
            double interes = saldo * tasaMensual;

            CuotaDTO cuota = new CuotaDTO();
            cuota.setNumeroCuota(i);
            cuota.setSaldoInicial(r2(saldo));
            cuota.setInteres(r2(interes));
            cuota.setAmortizacion(0.0);
            cuota.setCuotaTotal(r2(interes));
            cuota.setSaldoFinal(r2(saldo));
            cuota.setEsGraciaTotal(false);
            cuota.setEsGraciaParcial(true);

            cronograma.add(cuota);
        }

        // -------- MÉTODO FRANCÉS ----------
        int cuotasRestantes = n - graciaTotal - graciaParcial;

        double cuotaFija = 0.0;
        if (cuotasRestantes > 0) {
            // fórmula: A = P * i / (1 - (1+i)^-n)
            cuotaFija = saldo * (tasaMensual / (1 - Math.pow(1 + tasaMensual, -cuotasRestantes)));
        }

        for (int i = graciaTotal + graciaParcial + 1; i <= n; i++) {

            double interes = saldo * tasaMensual;
            double amortizacion = cuotaFija - interes;
            double saldoFinal = saldo - amortizacion;

            // evitar residuos negativos por decimales
            if (saldoFinal < 0 && saldoFinal > -0.01) saldoFinal = 0;

            CuotaDTO cuota = new CuotaDTO();
            cuota.setNumeroCuota(i);
            cuota.setSaldoInicial(r2(saldo));
            cuota.setInteres(r2(interes));
            cuota.setAmortizacion(r2(amortizacion));
            cuota.setCuotaTotal(r2(cuotaFija));
            cuota.setSaldoFinal(r2(saldoFinal));
            cuota.setEsGraciaTotal(false);
            cuota.setEsGraciaParcial(false);

            cronograma.add(cuota);

            saldo = saldoFinal;
        }

        // -------- VAN / TIR ----------
        double van = calcularVAN(monto, cronograma, tasaMensual);
        double tir = calcularTIR(monto, cronograma);

        // -------- Indicadores de transparencia ----------
        double interesesTotales = cronograma.stream().mapToDouble(CuotaDTO::getInteres).sum();
        double montoTotalPagado = cronograma.stream().mapToDouble(CuotaDTO::getCuotaTotal).sum();

        // TCEA anual efectiva (base) desde tasa mensual
        double tcea = (Math.pow(1 + tasaMensual, 12) - 1) * 100.0;

        // set outputs
        prestamoDTO.setCronograma(cronograma);
        prestamoDTO.setVan(r2(van));
        prestamoDTO.setTir(r6(tir)); // TIR suele verse con más precisión

        prestamoDTO.setCuotaFija(r2(cuotaFija));
        prestamoDTO.setInteresesTotales(r2(interesesTotales));
        prestamoDTO.setMontoTotalPagado(r2(montoTotalPagado));
        prestamoDTO.setTcea(r2(tcea));

        return prestamoDTO;
    }

    // -------------------- Conversión tasa --------------------
    private double convertirATasaMensual(Double tasaInput, String tipo, String capitalizacion) {

        if (tasaInput == null) throw new IllegalArgumentException("Tasa de interés inválida");

        // ✅ Acepta: 12 (porcentaje) o 0.12 (decimal)
        double tasa = (tasaInput >= 1.0) ? (tasaInput / 100.0) : tasaInput;

        if ("EFECTIVA".equalsIgnoreCase(tipo)) {
            // ✅ Asumimos TEA (Efectiva ANUAL) -> efectiva mensual
            return Math.pow(1.0 + tasa, 1.0 / 12.0) - 1.0;
        }

        if ("NOMINAL".equalsIgnoreCase(tipo)) {

            int m = 12; // por defecto MENSUAL
            if ("DIARIA".equalsIgnoreCase(capitalizacion)) m = 360;
            if ("ANUAL".equalsIgnoreCase(capitalizacion)) m = 1;

            // ✅ Asumimos TNA (Nominal ANUAL) con m capitalizaciones -> efectiva mensual
            return Math.pow(1.0 + (tasa / m), (double) m / 12.0) - 1.0;
        }

        throw new IllegalArgumentException("Tipo de tasa inválido (use EFECTIVA o NOMINAL)");
    }


    // -------------------- VAN --------------------
    private double calcularVAN(double monto, List<CuotaDTO> cronograma, double tasaMensual) {
        double van = -monto;
        int t = 1;
        for (CuotaDTO c : cronograma) {
            double flujo = c.getCuotaTotal() != null ? c.getCuotaTotal() : 0.0;
            van += flujo / Math.pow(1 + tasaMensual, t);
            t++;
        }
        return van;
    }

    // -------------------- TIR (Newton-Raphson) --------------------
    private double calcularTIR(double monto, List<CuotaDTO> cronograma) {

        double tir = 0.10;         // guess inicial 10% mensual (alto, pero converge rápido)
        double precision = 0.0000001;

        for (int i = 0; i < 2000; i++) {

            double van = -monto;
            double derivada = 0.0;

            for (int t = 0; t < cronograma.size(); t++) {
                double flujo = cronograma.get(t).getCuotaTotal() != null ? cronograma.get(t).getCuotaTotal() : 0.0;

                van += flujo / Math.pow(1 + tir, t + 1);
                derivada -= (t + 1) * flujo / Math.pow(1 + tir, t + 2);
            }

            // evitar división por 0
            if (Math.abs(derivada) < 1e-12) break;

            double nuevaTir = tir - van / derivada;

            if (Double.isNaN(nuevaTir) || Double.isInfinite(nuevaTir)) break;

            if (Math.abs(nuevaTir - tir) < precision) {
                return nuevaTir;
            }

            tir = nuevaTir;
        }

        return tir;
    }

    // -------------------- Helpers redondeo --------------------
    private double r2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private double r6(double v) {
        return Math.round(v * 1_000_000.0) / 1_000_000.0;
    }
}
