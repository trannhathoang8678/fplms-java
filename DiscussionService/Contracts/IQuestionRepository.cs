using DiscussionService.Dtos;
using DiscussionService.Helpers;
using DiscussionService.Models;

namespace DiscussionService.Contracts
{
    public interface IQuestionRepository : IRepositoryBase<Question>
    {
        Task<PagedList<Question>> GetAllQuestionsAsync(PaginationParams @params);
        Task<Question> GetQuestionByIdAsync(Guid questionId);
        Task<IEnumerable<Question>> GetQuestionsByStudentId(Guid studentId);
        void CreateQuestion(Question question);
        void UpdateQuestion(Question question);
        void DeleteQuestion(Question question);
    }
}