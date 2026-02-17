package com.upc.crediviviendaapi.services;

import com.upc.crediviviendaapi.dtos.CuotaDTO;
import com.upc.crediviviendaapi.dtos.PrestamoDTO;
import com.upc.crediviviendaapi.entities.Cliente;
import com.upc.crediviviendaapi.entities.Cuota;
import com.upc.crediviviendaapi.entities.Prestamo;
import com.upc.crediviviendaapi.entities.UnidadInmobiliaria;
import com.upc.crediviviendaapi.exceptions.ResourceNotFoundException;
import com.upc.crediviviendaapi.interfaces.IAuditService;
import com.upc.crediviviendaapi.interfaces.IPrestamoService;
import com.upc.crediviviendaapi.repositories.ClienteRepository;
import com.upc.crediviviendaapi.repositories.CuotaRepository;
import com.upc.crediviviendaapi.repositories.PrestamoRepository;
import com.upc.crediviviendaapi.repositories.UnidadInmobiliariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PrestamoService implements IPrestamoService {

    private final PrestamoRepository prestamoRepo;
    private final CuotaRepository cuotaRepo;
    private final ClienteRepository clienteRepo;
    private final UnidadInmobiliariaRepository unidadRepo;
    private final IAuditService audit; // ✅

    public PrestamoService(
            PrestamoRepository prestamoRepo,
            CuotaRepository cuotaRepo,
            ClienteRepository clienteRepo,
            UnidadInmobiliariaRepository unidadRepo,
            IAuditService audit
    ) {
        this.prestamoRepo = prestamoRepo;
        this.cuotaRepo = cuotaRepo;
        this.clienteRepo = clienteRepo;
        this.unidadRepo = unidadRepo;
        this.audit = audit;
    }

    @Override
    @Transactional(readOnly = true)
    public PrestamoDTO simular(PrestamoDTO dto) {
        Prestamo p = buildPrestamoFromDto(dto, false);
        SimResult res = calcularCronograma(p);

        // (Opcional) auditar simulaciones:
        // audit.log("SIMULATE", "PRESTAMO", null, "Simulación: monto=" + p.getMontoPrestamo() + ", plazo=" + p.getPlazoMeses());

        return toDto(p, res);
    }

    @Override
    public PrestamoDTO crear(PrestamoDTO dto) {
        Prestamo p = buildPrestamoFromDto(dto, true);

        // Generar cronograma + indicadores
        SimResult res = calcularCronograma(p);

        // Persistir préstamo
        Prestamo saved = prestamoRepo.save(p);

        // Persistir cuotas asociadas
        for (Cuota c : res.cuotas) {
            c.setPrestamo(saved);
        }
        cuotaRepo.saveAll(res.cuotas);

        // refrescar indicadores guardados
        saved.setCuotas(res.cuotas);
        saved.setCuotaFija(res.cuotaFija);
        saved.setInteresesTotales(res.interesesTotales);
        saved.setMontoTotalPagado(res.montoTotalPagado);
        saved.setVan(res.van);
        saved.setTir(res.tir);
        saved.setTcea(res.tcea);
        saved.setFechaSimulacion(res.fechaSimulacion);

        Prestamo saved2 = prestamoRepo.save(saved);

        // ✅ AUDITORÍA CREATE
        Long clienteId = (saved2.getCliente() != null ? saved2.getCliente().getId() : null);
        Long unidadId = (saved2.getUnidad() != null ? saved2.getUnidad().getId() : null);

        audit.log(
                "CREATE",
                "PRESTAMO",
                saved2.getId(),
                "Se creó préstamo: clienteId=" + clienteId
                        + ", unidadId=" + unidadId
                        + ", monto=" + saved2.getMontoPrestamo()
                        + ", plazoMeses=" + saved2.getPlazoMeses()
                        + ", moneda=" + saved2.getMoneda()
                        + ", tipoTasa=" + saved2.getTipoTasa()
                        + ", capitalizacion=" + saved2.getCapitalizacion()
                        + ", tasaInteres=" + saved2.getTasaInteres()
                        + ", tcea=" + saved2.getTcea()
                        + ", van=" + saved2.getVan()
                        + ", fechaSimulacion=" + saved2.getFechaSimulacion()
        );

        return toDto(saved2, res);
    }

    @Override
    @Transactional(readOnly = true)
    public PrestamoDTO obtenerPorId(Long id) {
        Prestamo p = prestamoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado: id=" + id));

        List<Cuota> cuotas = cuotaRepo.findByPrestamoIdOrderByNumeroCuota(id);

        SimResult res = new SimResult();
        res.cuotas = cuotas;
        res.cuotaFija = p.getCuotaFija();
        res.interesesTotales = p.getInteresesTotales();
        res.montoTotalPagado = p.getMontoTotalPagado();
        res.van = p.getVan();
        res.tir = p.getTir();
        res.tcea = p.getTcea();
        res.fechaSimulacion = p.getFechaSimulacion();

        return toDto(p, res);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoDTO> listar() {
        return prestamoRepo.findAll().stream()
                .map(p -> {
                    SimResult res = new SimResult();
                    res.cuotas = cuotaRepo.findByPrestamoIdOrderByNumeroCuota(p.getId());
                    res.cuotaFija = p.getCuotaFija();
                    res.interesesTotales = p.getInteresesTotales();
                    res.montoTotalPagado = p.getMontoTotalPagado();
                    res.van = p.getVan();
                    res.tir = p.getTir();
                    res.tcea = p.getTcea();
                    res.fechaSimulacion = p.getFechaSimulacion();
                    return toDto(p, res);
                })
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        Prestamo p = prestamoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado: id=" + id));

        // ✅ AUDITORÍA DELETE (antes de borrar)
        Long clienteId = (p.getCliente() != null ? p.getCliente().getId() : null);
        Long unidadId = (p.getUnidad() != null ? p.getUnidad().getId() : null);

        audit.log(
                "DELETE",
                "PRESTAMO",
                id,
                "Se eliminó préstamo: clienteId=" + clienteId
                        + ", unidadId=" + unidadId
                        + ", monto=" + p.getMontoPrestamo()
                        + ", plazoMeses=" + p.getPlazoMeses()
                        + ", moneda=" + p.getMoneda()
                        + ", fechaSimulacion=" + p.getFechaSimulacion()
        );

        // Borramos cuotas primero por seguridad
        List<Cuota> cuotas = cuotaRepo.findByPrestamoIdOrderByNumeroCuota(id);
        cuotaRepo.deleteAll(cuotas);

        prestamoRepo.deleteById(id);
    }
    // ----------------- Helpers -----------------

    private Prestamo buildPrestamoFromDto(PrestamoDTO dto, boolean requiereClienteUnidad) {
        Prestamo p = new Prestamo();

        if (requiereClienteUnidad) {
            Cliente cli = clienteRepo.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: id=" + dto.getClienteId()));
            UnidadInmobiliaria uni = unidadRepo.findById(dto.getUnidadInmobiliariaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada: id=" + dto.getUnidadInmobiliariaId()));
            p.setCliente(cli);
            p.setUnidad(uni);
        } else {
            // simular: si mandas ids, validamos
            if (dto.getClienteId() != null) {
                Cliente cli = clienteRepo.findById(dto.getClienteId())
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: id=" + dto.getClienteId()));
                p.setCliente(cli);
            }
            if (dto.getUnidadInmobiliariaId() != null) {
                UnidadInmobiliaria uni = unidadRepo.findById(dto.getUnidadInmobiliariaId())
                        .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada: id=" + dto.getUnidadInmobiliariaId()));
                p.setUnidad(uni);
            }
        }

        p.setMontoPrestamo(dto.getMontoPrestamo());
        p.setPlazoMeses(dto.getPlazoMeses());
        p.setMoneda(dto.getMoneda());
        p.setTipoTasa(dto.getTipoTasa());
        p.setCapitalizacion(dto.getCapitalizacion());
        p.setTasaInteres(dto.getTasaInteres());

        p.setGraciaTotal(dto.getGraciaTotal() == null ? 0 : dto.getGraciaTotal());
        p.setGraciaParcial(dto.getGraciaParcial() == null ? 0 : dto.getGraciaParcial());

        p.setFechaSimulacion(dto.getFechaSimulacion() != null ? dto.getFechaSimulacion() : LocalDate.now());

        return p;
    }

    private PrestamoDTO toDto(Prestamo p, SimResult res) {
        PrestamoDTO dto = new PrestamoDTO();

        dto.setId(p.getId());
        dto.setClienteId(p.getCliente() != null ? p.getCliente().getId() : null);
        dto.setUnidadInmobiliariaId(p.getUnidad() != null ? p.getUnidad().getId() : null);

        dto.setMontoPrestamo(p.getMontoPrestamo());
        dto.setPlazoMeses(p.getPlazoMeses());
        dto.setMoneda(p.getMoneda());
        dto.setTipoTasa(p.getTipoTasa());
        dto.setCapitalizacion(p.getCapitalizacion());
        dto.setTasaInteres(p.getTasaInteres());

        dto.setGraciaTotal(p.getGraciaTotal());
        dto.setGraciaParcial(p.getGraciaParcial());

        dto.setCuotaFija(res.cuotaFija);
        dto.setInteresesTotales(res.interesesTotales);
        dto.setMontoTotalPagado(res.montoTotalPagado);
        dto.setVan(res.van);
        dto.setTir(res.tir);
        dto.setTcea(res.tcea);
        dto.setFechaSimulacion(res.fechaSimulacion);

        List<CuotaDTO> cron = new ArrayList<>();
        if (res.cuotas != null) {
            for (Cuota c : res.cuotas) {
                CuotaDTO cd = new CuotaDTO();
                cd.setId(c.getId());
                cd.setNumeroCuota(c.getNumeroCuota());
                cd.setSaldoInicial(c.getSaldoInicial());
                cd.setAmortizacion(c.getAmortizacion());
                cd.setInteres(c.getInteres());
                cd.setCuotaTotal(c.getCuotaTotal());
                cd.setSaldoFinal(c.getSaldoFinal());
                cd.setEsGraciaTotal(c.getEsGraciaTotal());
                cd.setEsGraciaParcial(c.getEsGraciaParcial());
                cd.setFechaVencimiento(c.getFechaVencimiento());
                cron.add(cd);
            }
        }
        dto.setCronograma(cron);

        return dto;
    }

    // ====== Simulación: método francés + gracia + TECHO PROPIO ======

    private SimResult calcularCronograma(Prestamo p) {
        SimResult res = new SimResult();
        res.fechaSimulacion = p.getFechaSimulacion() != null ? p.getFechaSimulacion() : LocalDate.now();

        int n = p.getPlazoMeses() != null ? p.getPlazoMeses() : 0;
        int gt = p.getGraciaTotal() != null ? p.getGraciaTotal() : 0;
        int gp = p.getGraciaParcial() != null ? p.getGraciaParcial() : 0;

        if (n <= 0) throw new IllegalArgumentException("plazoMeses debe ser > 0");
        if (p.getMontoPrestamo() == null || p.getMontoPrestamo() <= 0)
            throw new IllegalArgumentException("montoPrestamo inválido");

        if (gt < 0 || gp < 0) throw new IllegalArgumentException("Las gracias no pueden ser negativas");
        if (gt + gp > n) throw new IllegalArgumentException("La suma de gracia total + parcial no puede superar el plazo");

        // Normalizar tasa: si viene 12 => 0.12
        double tasa = (p.getTasaInteres() != null ? p.getTasaInteres() : 0.0);
        if (tasa > 1.0) tasa = tasa / 100.0;

        double iMensual = tasaMensualEfectiva(
                tasa,
                safeUpper(p.getTipoTasa()),
                safeUpper(p.getCapitalizacion())
        );

        // ✅ MONTO BASE PARA CÁLCULO: aplica Techo Propio SIN cambiar el préstamo original
        double montoBase = p.getMontoPrestamo();

        if (p.getUnidad() != null
                && Boolean.TRUE.equals(p.getUnidad().getAplicaTechoPropio())
                && p.getUnidad().getBonoTechoPropio() != null
                && p.getUnidad().getBonoTechoPropio() > 0) {

            montoBase = Math.max(0.0, montoBase - p.getUnidad().getBonoTechoPropio());
        }

        double saldo = montoBase;
        List<Cuota> cuotas = new ArrayList<>();

        double interesesTot = 0.0;
        double totalPagado = 0.0;

        // 1) Gracia total (capitaliza interés)
        for (int k = 1; k <= gt; k++) {
            double interes = round2(saldo * iMensual);
            double saldoFinal = round2(saldo + interes);

            Cuota c = Cuota.builder()
                    .numeroCuota(k)
                    .saldoInicial(round2(saldo))
                    .amortizacion(0.0)
                    .interes(interes)
                    .cuotaTotal(0.0)
                    .saldoFinal(saldoFinal)
                    .esGraciaTotal(true)
                    .esGraciaParcial(false)
                    .fechaVencimiento(res.fechaSimulacion.plusDays(30L * k))
                    .build();

            cuotas.add(c);

            interesesTot += interes;
            saldo = saldoFinal;
        }

        // 2) Gracia parcial (paga solo interés)
        for (int k = gt + 1; k <= gt + gp; k++) {
            double interes = round2(saldo * iMensual);
            double cuota = interes;
            double saldoFinal = round2(saldo);

            Cuota c = Cuota.builder()
                    .numeroCuota(k)
                    .saldoInicial(round2(saldo))
                    .amortizacion(0.0)
                    .interes(interes)
                    .cuotaTotal(round2(cuota))
                    .saldoFinal(saldoFinal)
                    .esGraciaTotal(false)
                    .esGraciaParcial(true)
                    .fechaVencimiento(res.fechaSimulacion.plusDays(30L * k))
                    .build();

            cuotas.add(c);

            interesesTot += interes;
            totalPagado += cuota;
            saldo = saldoFinal;
        }

        // 3) Periodos normales franceses
        int restantes = n - gt - gp;
        if (restantes < 0) restantes = 0;

        double cuotaFija = 0.0;
        if (restantes > 0) {
            cuotaFija = round2(saldo * (iMensual / (1.0 - Math.pow(1.0 + iMensual, -restantes))));
        }

        for (int t = 1; t <= restantes; t++) {
            int num = gt + gp + t;

            double interes = round2(saldo * iMensual);
            double amort = round2(cuotaFija - interes);
            double saldoFinal = round2(saldo - amort);

            // último ajuste por redondeo
            if (t == restantes) {
                amort = round2(saldo);
                cuotaFija = round2(interes + amort);
                saldoFinal = 0.0;
            }

            Cuota c = Cuota.builder()
                    .numeroCuota(num)
                    .saldoInicial(round2(saldo))
                    .amortizacion(amort)
                    .interes(interes)
                    .cuotaTotal(cuotaFija)
                    .saldoFinal(saldoFinal)
                    .esGraciaTotal(false)
                    .esGraciaParcial(false)
                    .fechaVencimiento(res.fechaSimulacion.plusDays(30L * num))
                    .build();

            cuotas.add(c);

            interesesTot += interes;
            totalPagado += cuotaFija;
            saldo = saldoFinal;
        }

        res.cuotas = cuotas;
        res.cuotaFija = cuotaFija;
        res.interesesTotales = round2(interesesTot);
        res.montoTotalPagado = round2(totalPagado);

        // VAN/TIR/TCEA: flujos (t0 +montoBase, luego -cuotas)
        List<Double> flujos = new ArrayList<>();
        flujos.add(montoBase); // ✅ desembolso real financiado (con Techo Propio)
        for (Cuota c : cuotas) {
            flujos.add(-1.0 * (c.getCuotaTotal() != null ? c.getCuotaTotal() : 0.0));
        }

        double irrM = irrBiseccion(flujos, -0.999, 5.0, 1e-8, 200);
        double tcea = (irrM > -0.999) ? (Math.pow(1.0 + irrM, 12) - 1.0) : 0.0;

        double van = npv(iMensual, flujos);

        res.tir = round4(irrM);   // mensual (decimal)
        res.tcea = round4(tcea);  // anual (decimal)
        res.van = round2(van);

        // reflejar en préstamo (outputs)
        p.setCuotaFija(res.cuotaFija);
        p.setInteresesTotales(res.interesesTotales);
        p.setMontoTotalPagado(res.montoTotalPagado);
        p.setVan(res.van);
        p.setTir(res.tir);
        p.setTcea(res.tcea);
        p.setFechaSimulacion(res.fechaSimulacion);

        return res;
    }

    private double tasaMensualEfectiva(double tasaAnual, String tipoTasa, String cap) {
        // Asumimos que viene ANUAL (TEA o JNA). Convertimos a mensual efectiva.
        if ("NOMINAL".equals(tipoTasa)) {
            int m = switch (cap) {
                case "DIARIA" -> 360;
                case "MENSUAL" -> 12;
                case "ANUAL" -> 1;
                default -> 12;
            };
            return Math.pow(1.0 + (tasaAnual / m), (double) m / 12.0) - 1.0;
        }

        // EFECTIVA (TEA)
        return Math.pow(1.0 + tasaAnual, 1.0 / 12.0) - 1.0;
    }

    private static String safeUpper(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private static double round4(double v) {
        return Math.round(v * 10000.0) / 10000.0;
    }

    private static double npv(double rate, List<Double> cf) {
        double sum = 0.0;
        for (int t = 0; t < cf.size(); t++) {
            sum += cf.get(t) / Math.pow(1.0 + rate, t);
        }
        return sum;
    }

    private static double irrBiseccion(List<Double> cf, double lo, double hi, double eps, int iters) {
        double fLo = npv(lo, cf);
        double fHi = npv(hi, cf);

        if (fLo * fHi > 0) return 0.0;

        for (int i = 0; i < iters; i++) {
            double mid = (lo + hi) / 2.0;
            double fMid = npv(mid, cf);

            if (Math.abs(fMid) < eps) return mid;

            if (fLo * fMid < 0) {
                hi = mid;
                fHi = fMid;
            } else {
                lo = mid;
                fLo = fMid;
            }
        }
        return (lo + hi) / 2.0;
    }

    private static class SimResult {
        List<Cuota> cuotas = new ArrayList<>();
        LocalDate fechaSimulacion;

        Double cuotaFija;
        Double interesesTotales;
        Double montoTotalPagado;

        Double van;
        Double tir;
        Double tcea;
    }
}
