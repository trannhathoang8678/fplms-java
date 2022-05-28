using System.ComponentModel.DataAnnotations;

namespace DiscussionService.Dtos
{
    public record UpdateQuestionDto
    {
        [Required]
        [StringLength(250)]
        public string? Title { get; set; }

        [Required]
        [StringLength(1000)]
        public string? Content { get; set; }

        [Required]
        public Guid SubjectId { get; set; }
    }
}