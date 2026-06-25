package com.example.fashionshop.service;

import com.example.fashionshop.dto.AdminStatisticsResponse;
import com.example.fashionshop.dto.MonthlyRevenueResponse;
import com.example.fashionshop.dto.ProductStatisticResponse;
import com.example.fashionshop.entity.User;
import com.example.fashionshop.repository.AdminStatisticsRepository;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminStatisticsService {

    private final AdminStatisticsRepository adminStatisticsRepository;
    private final UserRepository userRepository;

    public AdminStatisticsService(AdminStatisticsRepository adminStatisticsRepository,
                                  UserRepository userRepository) {
        this.adminStatisticsRepository = adminStatisticsRepository;
        this.userRepository = userRepository;
    }

    public AdminStatisticsResponse getAdminStatistics(Long adminId,
                                                      Integer year,
                                                      Integer bestSellerMonths,
                                                      Integer noSaleMonths) {
        checkAdmin(adminId);

        int selectedYear = year != null ? year : getCurrentYear();
        int selectedBestSellerMonths = clampMonths(bestSellerMonths);
        int selectedNoSaleMonths = clampMonths(noSaleMonths);

        BigDecimal totalRevenue = adminStatisticsRepository.getTotalRevenueByYear(selectedYear);

        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        List<MonthlyRevenueResponse> monthlyRevenue = fillFull12Months(
                adminStatisticsRepository.getMonthlyRevenueByYear(selectedYear)
        );

        LocalDateTime endTime = getEndOfMonthRange();

        LocalDateTime bestSellerStartTime = getStartOfMonthRange(selectedBestSellerMonths);
        LocalDateTime noSaleStartTime = getStartOfMonthRange(selectedNoSaleMonths);

        List<ProductStatisticResponse> bestSellers = toProductStatisticResponses(
                adminStatisticsRepository.getBestSellers(bestSellerStartTime, endTime)
        );

        List<ProductStatisticResponse> noSaleProducts = toProductStatisticResponses(
                adminStatisticsRepository.getNoSaleProducts(noSaleStartTime, endTime)
        );

        return new AdminStatisticsResponse(
                selectedYear,
                totalRevenue,
                monthlyRevenue,
                selectedBestSellerMonths,
                bestSellers,
                selectedNoSaleMonths,
                noSaleProducts
        );
    }

    private void checkAdmin(Long adminId) {
        if (adminId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu adminId");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy người dùng"
                ));

        if (admin.getRole() == null || !admin.getRole().equalsIgnoreCase("ADMIN")) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không có quyền xem thống kê"
            );
        }
    }

    private int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    private int clampMonths(Integer months) {
        if (months == null) {
            return 1;
        }

        if (months < 1) {
            return 1;
        }

        if (months > 12) {
            return 12;
        }

        return months;
    }

    private LocalDateTime getStartOfMonthRange(int months) {
        LocalDate firstDayOfCurrentMonth = LocalDate.now().withDayOfMonth(1);
        return firstDayOfCurrentMonth.minusMonths(months - 1L).atStartOfDay();
    }

    private LocalDateTime getEndOfMonthRange() {
        LocalDate firstDayOfNextMonth = LocalDate.now().withDayOfMonth(1).plusMonths(1);
        return firstDayOfNextMonth.atStartOfDay();
    }

    private List<MonthlyRevenueResponse> fillFull12Months(List<Object[]> rows) {
        Map<Integer, BigDecimal> revenueByMonth = new HashMap<>();

        if (rows != null) {
            for (Object[] row : rows) {
                Integer month = toInteger(row[0]);
                BigDecimal revenue = toBigDecimal(row[1]);

                if (month != null && month >= 1 && month <= 12) {
                    revenueByMonth.put(month, revenue);
                }
            }
        }

        List<MonthlyRevenueResponse> result = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            result.add(new MonthlyRevenueResponse(
                    month,
                    revenueByMonth.getOrDefault(month, BigDecimal.ZERO)
            ));
        }

        return result;
    }

    private List<ProductStatisticResponse> toProductStatisticResponses(List<Object[]> rows) {
        List<ProductStatisticResponse> result = new ArrayList<>();

        if (rows == null) {
            return result;
        }

        for (Object[] row : rows) {
            result.add(new ProductStatisticResponse(
                    toLong(row[0]),
                    toStringValue(row[1]),
                    toStringValue(row[2]),
                    toBigDecimal(row[3]),
                    toBigDecimal(row[4]),
                    toStringValue(row[5]),
                    toLong(row[6])
            ));
        }

        return result;
    }

    private String toStringValue(Object value) {
        return value != null ? value.toString() : null;
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return Integer.parseInt(value.toString());
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        return Long.parseLong(value.toString());
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }

        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }

        return new BigDecimal(value.toString());
    }
}