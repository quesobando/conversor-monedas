package com.conversor.monedas.service;

import com.conversor.monedas.dto.ConvertRequest;
import com.conversor.monedas.dto.ConvertResponse;
import com.conversor.monedas.dto.FrankfurterLatestResponse;
import com.conversor.monedas.dto.FrankfurterTimeSeriesResponse;
import com.conversor.monedas.dto.TimeSeriesPointDto;
import com.conversor.monedas.dto.TimeSeriesResponse;
import com.conversor.monedas.model.ConversionHistory;
import com.conversor.monedas.repository.ConversionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConversionService {

    private final FrankfurterService frankfurterService;
    private final ConversionHistoryRepository historyRepository;

    public Map<String, String> listCurrencies() {
        return frankfurterService.getCurrencies();
    }

    public Map<String, BigDecimal> listLatestRates(String base) {
        return frankfurterService.getLatestRates(base.toUpperCase(Locale.ROOT)).rates;
    }

    public ConvertResponse convert(ConvertRequest request) {
        String from = request.getFrom().toUpperCase(Locale.ROOT);
        String to = request.getTo().toUpperCase(Locale.ROOT);

        FrankfurterLatestResponse latest = frankfurterService.getLatestRate(from, to);
        BigDecimal rate = latest.rates.get(to);
        if (rate == null) {
            throw new IllegalArgumentException("No se encontró la tasa para " + from + " -> " + to);
        }

        BigDecimal convertedAmount = request.getAmount().multiply(rate).setScale(4, RoundingMode.HALF_UP);

        // Guardar en el historial (Neon)
        ConversionHistory entity = ConversionHistory.builder()
                .fromCurrency(from)
                .toCurrency(to)
                .amount(request.getAmount())
                .rate(rate)
                .convertedAmount(convertedAmount)
                .build();
        historyRepository.save(entity);

        return ConvertResponse.builder()
                .from(from)
                .to(to)
                .amount(request.getAmount())
                .rate(rate)
                .convertedAmount(convertedAmount)
                .date(latest.date)
                .build();
    }

    public Page<ConversionHistory> getHistory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return historyRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public TimeSeriesResponse getVariation(String from, String to, LocalDate start, LocalDate end) {
        String f = from.toUpperCase(Locale.ROOT);
        String t = to.toUpperCase(Locale.ROOT);
        FrankfurterTimeSeriesResponse series = frankfurterService.getTimeSeries(f, t, start, end);

        List<TimeSeriesPointDto> points = new ArrayList<>();
        series.rates.forEach((date, rates) -> {
            BigDecimal rate = rates.get(t);
            if (rate != null) {
                points.add(new TimeSeriesPointDto(date, rate));
            }
        });
        points.sort((a, b) -> a.getDate().compareTo(b.getDate()));

        return new TimeSeriesResponse(f, t, points);
    }
}
