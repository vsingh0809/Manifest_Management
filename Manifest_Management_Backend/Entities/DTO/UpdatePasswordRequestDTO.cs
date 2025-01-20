using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ManifestPortal1.Entities.DTO
{
    public class UpdatePasswordRequestDTO
    {
        public int Id { get; set; }
        public string NewPassword { get; set; }

    }
}
