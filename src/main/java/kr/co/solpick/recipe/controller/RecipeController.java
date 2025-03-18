package kr.co.solpick.recipe.controller;

import kr.co.solpick.external.recipick.dto.RecipickLikeResponseDTO;
import kr.co.solpick.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/member/recipe")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000/"})
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping("/likes")
    public ResponseEntity<List<RecipickLikeResponseDTO>> getLikedRecipes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Map<String, Object> claims = (Map<String, Object>) principal;

        Object idObj = claims.get("recipickUserId");
        Integer memberId = (idObj instanceof Double) ?
                ((Double) idObj).intValue() :
                (Integer) idObj;

        log.info("좋아요 레시피 목록 요청 수신: recipickmemberId={}", memberId);

        List<RecipickLikeResponseDTO> likedRecipes = recipeService.getLikedRecipes(memberId);

        if (likedRecipes.isEmpty()) {
            log.info("좋아요 레시피 없음: recipickmemberId={}", memberId);
            return ResponseEntity.noContent().build();
        }

        log.info("좋아요 레시피 목록 조회 성공: memberId={}, 건수={}", memberId, likedRecipes.size());
        return ResponseEntity.ok(likedRecipes);
    }
}