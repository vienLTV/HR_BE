package org.microboy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {
	public List<T> items;
	public int currentPage;
	public int pageSize;
	public long totalItems;
	public int totalPages;
}
