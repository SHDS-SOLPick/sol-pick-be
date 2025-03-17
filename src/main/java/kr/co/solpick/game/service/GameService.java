package kr.co.solpick.game.service;

import kr.co.solpick.game.dto.*;
import kr.co.solpick.game.entity.CompletedRecipe;
import kr.co.solpick.game.entity.DiscoveredIngredients;
import kr.co.solpick.game.entity.GameState;
import kr.co.solpick.game.entity.GameUserRecipe;
import kr.co.solpick.game.repository.CompletedRecipeRepository;
import kr.co.solpick.game.repository.DiscoveredIngredientsRepository;
import kr.co.solpick.game.repository.GameStateRepository;
import kr.co.solpick.game.repository.GameUserRecipeRepository;
//import kr.co.solpick.point.service.PointService; // 추후 구현
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameUserRecipeRepository gameUserRecipeRepository;
    private final GameStateRepository gameStateRepository;
    private final DiscoveredIngredientsRepository discoveredIngredientsRepository;
    private final CompletedRecipeRepository completedRecipeRepository;
//    private final PointService pointService; // 포인트 서비스는 기존에 구현되어 있다고 가정

    /**
     * 유저의 레시피 선택 저장
     * @param userId 유저 ID
     * @param recipeId 레시피 ID
     * @return 저장된 GameUserRecipe
     */
    @Transactional
    public GameUserRecipe saveGameUserRecipe(Integer userId, Integer recipeId) {
        // 유저 레시피 선택 저장
        GameUserRecipe gameUserRecipe = GameUserRecipe.builder()
                .userId(userId)
                .gameRecipeId(recipeId)
                .selectedAt(LocalDateTime.now())
                .build();

        return gameUserRecipeRepository.save(gameUserRecipe);
    }

    /**
     * 유저가 선택한 레시피 조회
     * @param userId 유저 ID
     * @return 레시피 ID
     */
    public Optional<Integer> getSelectedRecipe(Integer userId) {
        return gameUserRecipeRepository.findTopByUserIdOrderBySelectedAtDesc(userId)
                .map(GameUserRecipe::getGameRecipeId);
    }

    /**
     * 유저의 게임 상태 조회 또는 생성
     * @param userId 유저 ID
     * @return 게임 상태
     */
    @Transactional
    public GameState getOrCreateGameState(Integer userId) {
        return gameStateRepository.findByUserId(userId)
                .orElseGet(() -> {
                    GameState newState = GameState.builder()
                            .userId(userId)
                            .level(1)
                            .currentExp(0)
                            .energy(100)
                            .food(5)
                            .ingredientsCount(0)
                            .build();
                    return gameStateRepository.save(newState);
                });
    }

    /**
     * 게임 상태 업데이트
     * @param userId 유저 ID
     * @param gameStateDTO 게임 상태 DTO
     * @return 업데이트된 게임 상태
     */
    @Transactional
    public GameState updateGameState(Integer userId, GameStateDTO gameStateDTO) {
        GameState gameState = getOrCreateGameState(userId);

        // 필드 업데이트
        gameState.setLevel(gameStateDTO.getLevel());
        gameState.setCurrentExp(gameStateDTO.getCurrentExp());
        gameState.setEnergy(gameStateDTO.getEnergy());
        gameState.setFood(gameStateDTO.getFood());
        gameState.setIngredientsCount(gameStateDTO.getIngredientsCount());

        return gameStateRepository.save(gameState);
    }

    /**
     * 식재료 발견 정보 초기화
     * @param userId 유저 ID
     * @param recipeId 레시피 ID
     * @param ingredients 식재료 정보 목록
     */
    @Transactional
    public void initializeGameDiscoveredIngredients(
            Integer userId, Integer recipeId, List<IngredientInfoDTO> ingredients) {

        // 기존 식재료 정보 삭제
        discoveredIngredientsRepository.deleteByUserIdAndRecipeId(userId, recipeId);

        // 새로운 식재료 정보 추가
        ingredients.forEach(ingredientInfo -> {
            DiscoveredIngredients ingredient = DiscoveredIngredients.builder()
                    .userId(userId)
                    .recipeId(recipeId)
                    .ingredientName(ingredientInfo.getName())
                    .discovered(false)
                    .count(0)
                    .requiredQuantity(ingredientInfo.getRequiredQuantity())
                    .build();

            discoveredIngredientsRepository.save(ingredient);
        });
    }

    /**
     * 식재료 발견 처리
     * @param userId 유저 ID
     * @param recipeId 레시피 ID
     * @param ingredientName 식재료 이름
     * @return 발견 결과
     */
    @Transactional
    public DiscoveryResultDTO discoverIngredient(Integer userId, Integer recipeId, String ingredientName) {
        // 식재료 조회
        DiscoveredIngredients ingredient = discoveredIngredientsRepository
                .findByUserIdAndRecipeIdAndIngredientName(userId, recipeId, ingredientName)
                .orElseThrow(() -> new RuntimeException("Ingredient not found: " + ingredientName));

        boolean isNewlyDiscovered = !ingredient.getDiscovered();

        // 식재료 발견 처리
        ingredient.setDiscovered(true);
        ingredient.setCount(ingredient.getCount() + 1);
        discoveredIngredientsRepository.save(ingredient);

        // 게임 상태 업데이트
        GameState gameState = getOrCreateGameState(userId);
        gameState.setIngredientsCount(gameState.getIngredientsCount() + 1);
        gameStateRepository.save(gameState);

        // 레시피 완성 여부 체크
        boolean isRecipeCompleted = checkRecipeCompletion(userId, recipeId);

        return DiscoveryResultDTO.builder()
                .isNewlyDiscovered(isNewlyDiscovered)
                .newCount(ingredient.getCount())
                .isRecipeCompleted(isRecipeCompleted)
                .build();
    }

    /**
     * 레시피 완성 여부 체크
     * @param userId 유저 ID
     * @param recipeId 레시피 ID
     * @return 완성 여부
     */
    private boolean checkRecipeCompletion(Integer userId, Integer recipeId) {
        // 식재료 목록 조회
        List<DiscoveredIngredients> ingredients =
                discoveredIngredientsRepository.findByUserIdAndRecipeId(userId, recipeId);

        // 모든 식재료가 필요 수량만큼 모였는지 확인
        boolean isCompleted = ingredients.stream()
                .allMatch(ing -> ing.getDiscovered() && ing.getCount() >= ing.getRequiredQuantity());

        if (isCompleted) {
            // 이미 완성된 레시피인지 확인
            boolean alreadyCompleted = completedRecipeRepository
                    .existsByUserIdAndRecipeId(userId, recipeId);

            if (!alreadyCompleted) {
                // 완성된 레시피 저장
                CompletedRecipe completedRecipe = CompletedRecipe.builder()
                        .userId(userId)
                        .recipeId(recipeId)
                        .pointAmount(500) // 포인트 금액 설정 (필요에 따라 조정)
                        .build();
                completedRecipeRepository.save(completedRecipe);

                // 포인트 적립
//                pointService.addPoints(userId, 500, "레시피 완성 보상");

                return true;
            }
        }

        return false;
    }

    /**
     * 사료 추가
     * @param userId 유저 ID
     * @param amount 추가할 사료 수량
     */
    @Transactional
    public void addFood(Integer userId, int amount) {
        GameState gameState = getOrCreateGameState(userId);
        gameState.setFood(gameState.getFood() + amount);
        gameStateRepository.save(gameState);
    }

    /**
     * 유저의 발견한 식재료 목록 조회
     * @param userId 유저 ID
     * @param recipeId 레시피 ID
     * @return 발견한 식재료 목록
     */
    public List<DiscoveredIngredientsDTO> getDiscoveredIngredients(Integer userId, Integer recipeId) {
        List<DiscoveredIngredients> ingredients =
                discoveredIngredientsRepository.findByUserIdAndRecipeId(userId, recipeId);

        return ingredients.stream()
                .map(ingredient -> DiscoveredIngredientsDTO.builder()
                        .ingredientName(ingredient.getIngredientName())
                        .discovered(ingredient.getDiscovered())
                        .count(ingredient.getCount())
                        .requiredQuantity(ingredient.getRequiredQuantity())
                        .build())
                .collect(Collectors.toList());
    }
}