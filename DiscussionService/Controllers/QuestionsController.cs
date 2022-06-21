using System.Data;
using AutoMapper;
using DiscussionService.Filters;
using DiscussionService.Contracts;
using DiscussionService.Dtos;
using DiscussionService.Models;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;

namespace DiscussionService.Controllers
{
    [ApiController]
    [Route("api/discussion/questions")]
    [TypeFilter(typeof(AuthorizationFilterAttribute))]
    public class QuestionsController : ControllerBase
    {

        private IMapper _mapper;
        private IRepositoryWrapper _repositoryWrapper;

        public QuestionsController(IMapper mapper, IRepositoryWrapper repositoryWrapper)
        {
            _mapper = mapper;
            _repositoryWrapper = repositoryWrapper;
        }

        [HttpGet]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        public async Task<IActionResult> GetAllQuestions([FromQuery] QuestionsQueryStringParameters queryStringParameters)
        {
            try
            {
                //var userEmail = HttpContext.Items["userEmail"] as string;
                //var userRole = HttpContext.Items["userRole"] as string;

                var questions = await _repositoryWrapper.QuestionRepository.GetAllQuestionsAsync(queryStringParameters);

                var metadata = new
                {
                    questions.TotalPages,
                    questions.TotalCount,
                    questions.PageSize,
                    questions.CurrentPage,
                    questions.HasPrevious,
                    questions.HasNext
                };

                Response.Headers.Add("X-Pagination", JsonConvert.SerializeObject(metadata));
                var result = _mapper.Map<List<GetQuestionDto>>(questions);
                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpGet("{questionId}")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        public async Task<IActionResult> GetQuestionAnswers(Guid questionId)
        {
            try
            {
                var question = await _repositoryWrapper.QuestionRepository.GetQuestionByIdAsync(questionId, "eager");
                var result = _mapper.Map<GetQuestionDto>(question);

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpPost]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> CreateQuestion([FromBody] CreateQuestionDto createQuestionDto)
        {
            try
            {
                var userEmail = HttpContext.Items["UserEmail"] as string;
                var userRole = HttpContext.Items["UserRole"] as string;

                if (!userRole.Equals("Student"))
                {
                    return Forbid("Only student can create questions.");
                }
                Question question = _mapper.Map<Question>(createQuestionDto);
                var student = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(userEmail);
                var subject = await _repositoryWrapper.SubjectRepository.GetSubjectByNameAsync(createQuestionDto.SubjectName);
                question.Id = Guid.NewGuid();
                question.SubjectId = subject.Id;
                question.StudentId = student.Id;

                _repositoryWrapper.QuestionRepository.CreateQuestion(question);
                await _repositoryWrapper.SaveAsync();
                return Created("~api/discussion/questions/" + question.Id, createQuestionDto);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpPut("{questionId}")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> UpdateQuestion(Guid questionId, [FromBody] UpdateQuestionDto updateQuestionDto)
        {
            try
            {

                var userEmail = HttpContext.Items["UserEmail"] as string;
                var userRole = HttpContext.Items["UserRole"] as string;

                if (!userRole.Equals("Student"))
                {
                    return Forbid("Only student can update questions.");
                }

                var student = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(userEmail);
                var newQuestion = _mapper.Map<Question>(updateQuestionDto);
                var subject = await _repositoryWrapper.SubjectRepository.GetSubjectByNameAsync(updateQuestionDto.SubjectName);
                var question = await _repositoryWrapper.QuestionRepository.GetQuestionByIdAsync(questionId);

                if (question == null)
                {
                    return NotFound();
                }

                if (!student.Id.Equals(question.StudentId))
                {
                    return Forbid("Only the author of the question can update the question");
                }

                if (subject == null)
                {
                    return NotFound("Subject doesn't exists");
                }

                question.Title = newQuestion.Title;
                question.Content = newQuestion.Content;
                question.ModifiedDate = DateTime.Now;
                question.SubjectId = subject.Id;
                _repositoryWrapper.QuestionRepository.UpdateQuestion(question);
                await _repositoryWrapper.SaveAsync();
                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpDelete("{questionId}")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> DeleteQuestion([FromRoute] Guid questionId)
        {
            try
            {
                var userEmail = HttpContext.Items["UserEmail"] as string;
                var userRole = HttpContext.Items["UserRole"] as string;
                var student = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(userEmail);
                var question = await _repositoryWrapper.QuestionRepository.GetQuestionByIdAsync(questionId);

                if (question == null)
                {
                    return NotFound();
                }

                if (!student.Id.Equals(question.StudentId) && !userRole.Equals("Lecturer"))
                {
                    return Forbid("Only the author of the question or a lecturer can delete the question");
                }

                question.Removed = true;
                question.RemovedBy = userEmail;
                _repositoryWrapper.QuestionRepository.UpdateQuestion(question);
                await _repositoryWrapper.SaveAsync();
                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }
    }
}