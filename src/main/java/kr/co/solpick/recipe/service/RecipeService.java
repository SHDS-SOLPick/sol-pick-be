package kr.co.solpick.recipe.service;

import kr.co.solpick.external.recipick.client.RecipickRecipeApiClient;
import kr.co.solpick.external.recipick.dto.RecipickLikeResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipickRecipeApiClient recipickRecipeApiClient;

    public List<RecipickLikeResponseDTO> getLikedRecipes(int memberId) {
        log.info("좋아요 레시피 목록 조회 서비스 호출: memberId={}", memberId);
        return recipickRecipeApiClient.getLikedRecipes(memberId);
    }
}