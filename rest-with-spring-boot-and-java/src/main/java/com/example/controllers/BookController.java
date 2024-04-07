package com.example.controllers;

import com.example.data.vo.v1.BookVO;
import com.example.services.BookServices;
import com.example.util.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/book/v1")
@Tag(name = "Books", description = "Endpoints for Management Books")
public class BookController {
    @Autowired
    BookServices service;

    @GetMapping(produces = {com.example.util.MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Finds all Books", description = "Finds all Books",
            tags = {"Books"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookVO.class)))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204",content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400",content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404",content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<PagedModel<EntityModel<BookVO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ){
        Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, "title"));

        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(summary = "Find Book by Id", description = "Finds Book by Id",
            tags = {"Books"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = BookVO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204",content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400",content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404",content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)

            })
    @GetMapping(value = "/{id}",
                produces = {com.example.util.MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    public BookVO findById(@PathVariable(value = "id") Long id){
        return service.findById(id);
    }

    @PostMapping(
            consumes = {com.example.util.MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            produces = {com.example.util.MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML}
    )
    @Operation(summary = "Adds a new Book",
            description = "Adds a new Book by passing in a JSON, XML or YML representation of the book!",
            tags = {"Books"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = BookVO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400",content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)

            }
    )
    public BookVO create(@RequestBody BookVO book){
        return service.create(book);
    }

    @PutMapping(
            consumes = {com.example.util.MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            produces = {com.example.util.MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML}
    )
    @Operation(summary = "Updates a Book",
            description = "Updates a Book by passing in a JSON, XML or YML representation of the Book!",
            tags = {"Books"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = BookVO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400",content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404",content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)

            }
    )
    public BookVO update(@RequestBody BookVO book){
        return service.update(book);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deletes Book by Id", description = "Deletes a Book by ID",
            tags = {"Books"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400",content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404",content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)

            }
    )
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
