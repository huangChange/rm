package com.shell.common.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.shell.common.utils.JsonUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/19 20:11
 * @Description
 */
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"pageNumber", "pageSize", "pageCount", "total", "prev", "next", "records"})
public class ApiPage<T> extends ApiObject implements IPage<T> {

    private static final Long serialVersionUID = 1L;

    /**
     * 查询数据列表
     */
    private List<T> records = Collections.emptyList();

    /**
     * 总数
     */
    private long total;

    /**
     * 每页显示条数 默认为10
     */
    private long size = 10L;

    /**
     * 当前页 默认为1
     */
    private long current = 1;

    /**
     * 排序字段信息
     */
    @JsonIgnore
    private List<OrderItem> orders = new ArrayList<>();

    /**
     * 自动优化 COUNT SQL
     */
    @JsonIgnore
    private boolean optimizeCountSql = true;

    /**
     * 是否进行 count 查询
     */
    @JsonIgnore
    private boolean isSearchCount = true;

    /**
     *
     * @param page
     * @param records
     * @return
     * @param <A> type A
     * @param <B> type B
     */
    public static <A, B> ApiPage<B> to(IPage<A> page, List<B> records) {
        ApiPage<B> apiPage = buildIPage(page);
        apiPage.setRecords(records);
        return apiPage;
    }

    public static <A, B> ApiPage<B> to(IPage<A> page, Function<List<A>, List<B>> func) {
        ApiPage<B> apiPage = buildIPage(page);
        apiPage.setRecords(func.apply(page.getRecords()));
        return apiPage;
    }

    public static <B> ApiPage<B> to(IPage<Map<String, Object>> page, Class<B> typeClass) {
        ApiPage<B> apiPage = buildIPage(page);
        apiPage.setRecords(page.getRecords().stream().map(map -> JsonUtils.convertValue(map, typeClass)).toList());
        return apiPage;
    }

    private static <B> ApiPage<B> buildIPage(IPage<?> page) {
        ApiPage<B> apiPage = new ApiPage<>();
        if (Objects.isNull(page)) {
            return apiPage;
        }
        apiPage.setCurrent(page.getCurrent());
        apiPage.setSize(page.getSize());
        apiPage.setTotal(page.getTotal());
        apiPage.setPages(page.getPages());
        apiPage.setOrders(page.orders());
        apiPage.setSearchCount(page.searchCount());
        apiPage.setOptimizeCountSql(page.optimizeCountSql());
        return apiPage;
    }

    /**
     * 当前页码 从1开始
     * @return
     */
    public final long getPageNumber() {
        return getCurrent();
    }

    /**
     * 页面大小 每页10条数据
     * @return
     */
    public final long getPageSize() {
        return getSize();
    }

    /**
     * 页面数量
     * @return
     */
    public final long getPageCount() {
        return getPages();
    }

    /**
     * 总记录数量
     * @return
     */
    public final long getTotal() {
        return this.total;
    }

    /**
     * 是否有上一页
     * @return
     */
    public boolean getPrev() {
        return hasPrevious();
    }

    /**
     * 是否有下一页
     * @return
     */
    public boolean getNext() {
        return hasNext();
    }

    /**
     * 记录列表
     * @return
     */
    public List<T> getRecords() {
        return this.records;
    }

    /**
     * 是否存在上一页
     * @return
     */
    public boolean hasPrevious() {
        return this.current > 1;
    }

    /**
     * 是否存在下一页
     * @return
     */
    public boolean hasNext() {
        return this.current < this.getPages();
    }

    @Override
    public ApiPage<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    @Override
    public ApiPage<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public ApiPage<T> setSize(long size) {
        this.size = size;
        return this;
    }

    @Override
    public long getCurrent() {
        return this.current;
    }

    @Override
    public ApiPage<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    @JsonIgnore
    @Override
    public long getPages() {
        return IPage.super.getPages();
    }

    /**
     * 添加新的排序条件
     * @return
     */
    public ApiPage<T> addOrder(OrderItem ...orderItems) {
        orders.addAll(List.of(orderItems));
        return this;
    }

    @Override
    public List<OrderItem> orders() {
        return getOrders();
    }

    public List<OrderItem> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderItem> orders) {
        this.orders = orders;
    }

    @JsonIgnore
    @Override
    public boolean searchCount() {
        if (total < 0) {
            return false;
        }
        return isSearchCount;
    }

    public ApiPage<T> setSearchCount(boolean isSearchCount) {
        this.isSearchCount = isSearchCount;
        return this;
    }

    public ApiPage<T> setOptimizeCountSql(boolean optimizeCountSql) {
        this.optimizeCountSql = optimizeCountSql;
        return this;
    }
}
